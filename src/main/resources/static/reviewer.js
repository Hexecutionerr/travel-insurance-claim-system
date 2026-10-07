/* =========================================================
   reviewer.js — Reviewer Portal JavaScript
   Handles: Load Pending Claims, View Claim, Approve, Reject
   ========================================================= */

const API = '/api/reviewer';

// Track which claim is currently open for review
let currentClaimId = null;

/* ---------- Tab Navigation ---------- */
function showReviewerTab(tabId, btn) {
    document.querySelectorAll('.tab-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('nav button').forEach(b => b.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    if (btn) btn.classList.add('active');
}

/* ---------- Utility: Show message ---------- */
function showMsg(elementId, message, type) {
    const el = document.getElementById(elementId);
    el.className = `msg msg-${type} show`;
    el.innerHTML = message;
}

function hideMsg(elementId) {
    const el = document.getElementById(elementId);
    el.className = 'msg';
    el.innerHTML = '';
}

/* ---------- Utility: Status Badge ---------- */
function statusBadge(status) {
    const map = {
        'PENDING': 'badge-pending',
        'APPROVED': 'badge-approved',
        'REJECTED': 'badge-rejected'
    };
    return `<span class="badge ${map[status] || ''}">${status}</span>`;
}

/* ---------- Utility: Format DateTime ---------- */
function formatDateTime(isoString) {
    if (!isoString) return '—';
    const d = new Date(isoString);
    return d.toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' });
}


/* =========================================================
   LOAD PENDING CLAIMS
   ========================================================= */
async function loadPendingClaims() {
    hideMsg('pendingMsg');
    const listEl = document.getElementById('claimsList');
    const btn = document.getElementById('refreshBtn');

    btn.disabled = true;
    btn.textContent = '⏳ Loading...';
    listEl.innerHTML = '<div class="empty-state">Loading pending claims...</div>';

    try {
        const response = await fetch(`${API}/claims/pending`);
        const data = await response.json();

        if (response.status === 200) {
            if (data.length === 0) {
                listEl.innerHTML = '<div class="empty-state">✅ No pending claims at this time.</div>';
            } else {
                listEl.innerHTML = '';
                data.forEach(claim => {
                    const item = document.createElement('div');
                    item.className = 'claim-list-item';
                    item.innerHTML = `
                        <div>
                            <div class="claim-id">Claim #${claim.claimId}</div>
                            <div class="claim-info">${claim.customerName} — ${claim.claimType}</div>
                        </div>
                        <div style="text-align:right;">
                            <div style="font-weight:600;color:#1a3a5c;">₹${Number(claim.claimAmount).toLocaleString('en-IN')}</div>
                            <div class="claim-meta">${claim.destination} | ${claim.travelDate}</div>
                        </div>
                        <span class="badge badge-pending">PENDING</span>
                    `;
                    item.onclick = () => openClaimForReview(claim.claimId);
                    listEl.appendChild(item);
                });
                showMsg('pendingMsg', `📋 Found <strong>${data.length}</strong> pending claim(s). Click a claim to review it.`, 'info');
            }
        } else {
            showMsg('pendingMsg', '❌ Failed to load pending claims.', 'error');
            listEl.innerHTML = '<div class="empty-state">Failed to load claims.</div>';
        }

    } catch (err) {
        showMsg('pendingMsg', '❌ Network error. Make sure the server is running.', 'error');
        listEl.innerHTML = '<div class="empty-state">Network error.</div>';
        console.error('Load pending error:', err);
    } finally {
        btn.disabled = false;
        btn.textContent = '🔄 Refresh List';
    }
}


/* =========================================================
   OPEN CLAIM FOR REVIEW
   ========================================================= */
async function openClaimForReview(claimId) {
    currentClaimId = claimId;
    hideMsg('detailMsg');
    document.getElementById('reviewDetailsCard').style.display = 'none';

    // Switch to the detail tab
    showReviewerTab('detailTab', document.getElementById('navDetail'));

    try {
        const response = await fetch(`${API}/claims/${claimId}`);
        const claim = await response.json();

        if (response.status === 200) {
            populateReviewDetails(claim);
            document.getElementById('reviewDetailsCard').style.display = 'block';
            resetActionButtons(claim.status);
        } else {
            showMsg('detailMsg', `❌ Could not load claim #${claimId}.`, 'error');
        }

    } catch (err) {
        showMsg('detailMsg', '❌ Network error while loading claim details.', 'error');
        console.error('Open claim error:', err);
    }
}

/* ---------- Fill the detail fields ---------- */
function populateReviewDetails(claim) {
    document.getElementById('r-claimId').textContent          = claim.claimId;
    document.getElementById('r-status').innerHTML             = statusBadge(claim.status);
    document.getElementById('r-policyNumber').textContent     = claim.policyNumber;
    document.getElementById('r-customerName').textContent     = claim.customerName;
    document.getElementById('r-destination').textContent      = claim.destination;
    document.getElementById('r-travelDate').textContent       = claim.travelDate;
    document.getElementById('r-claimType').textContent        = claim.claimType;
    document.getElementById('r-claimAmount').textContent      = '₹ ' + Number(claim.claimAmount).toLocaleString('en-IN');
    document.getElementById('r-description').textContent      = claim.description;
    document.getElementById('r-documentReference').textContent= claim.documentReference;
    document.getElementById('r-createdAt').textContent        = formatDateTime(claim.createdAt);
    document.getElementById('r-updatedAt').textContent        = formatDateTime(claim.updatedAt);
}

/* ---------- Disable action buttons if claim is already decided ---------- */
function resetActionButtons(status) {
    const actionSection = document.getElementById('actionSection');
    cancelReject(); // Reset reject box

    if (status !== 'PENDING') {
        // Already decided: hide action buttons, show info
        actionSection.innerHTML = `
            <div class="msg msg-info show">
                ℹ️ This claim has already been <strong>${status}</strong>. No further action is required.
            </div>`;
    }
}


/* =========================================================
   APPROVE CLAIM
   ========================================================= */
async function approveClaim() {
    if (!currentClaimId) return;

    const btn = document.getElementById('approveBtn');
    btn.disabled = true;
    btn.textContent = '⏳ Approving...';

    try {
        const response = await fetch(`${API}/claims/${currentClaimId}/approve`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await response.json();

        if (response.status === 200) {
            showMsg('detailMsg', `✅ Claim #${currentClaimId} has been <strong>APPROVED</strong> successfully.`, 'success');
            document.getElementById('r-status').innerHTML = statusBadge(data.status);
            document.getElementById('r-updatedAt').textContent = formatDateTime(data.updatedAt);
            resetActionButtons(data.status);
        } else {
            showMsg('detailMsg', `❌ Failed to approve claim: ${data.message || 'Unknown error.'}`, 'error');
        }

    } catch (err) {
        showMsg('detailMsg', '❌ Network error while approving claim.', 'error');
        console.error('Approve error:', err);
    } finally {
        btn.disabled = false;
        btn.textContent = '✅ Approve';
    }
}


/* =========================================================
   REJECT CLAIM — Toggle & Confirm
   ========================================================= */
function toggleRejectBox() {
    const box = document.getElementById('rejectReasonBox');
    const confirmBtn = document.getElementById('confirmRejectBtn');
    const cancelBtn = document.getElementById('cancelRejectBtn');
    const toggleBtn = document.getElementById('rejectToggleBtn');

    box.style.display = 'block';
    confirmBtn.style.display = 'inline-flex';
    cancelBtn.style.display = 'inline-flex';
    toggleBtn.style.display = 'none';
    document.getElementById('rejectionReason').focus();
}

function cancelReject() {
    document.getElementById('rejectReasonBox').style.display = 'none';
    document.getElementById('confirmRejectBtn').style.display = 'none';
    document.getElementById('cancelRejectBtn').style.display = 'none';
    document.getElementById('rejectToggleBtn').style.display = 'inline-flex';
    document.getElementById('rejectionReason').value = '';
}

async function rejectClaim() {
    if (!currentClaimId) return;

    const reason = document.getElementById('rejectionReason').value.trim();
    if (!reason) {
        showMsg('detailMsg', '⚠️ Please provide a rejection reason before confirming.', 'error');
        return;
    }

    const btn = document.getElementById('confirmRejectBtn');
    btn.disabled = true;
    btn.textContent = '⏳ Rejecting...';

    try {
        const response = await fetch(`${API}/claims/${currentClaimId}/reject`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ rejectionReason: reason })
        });
        const data = await response.json();

        if (response.status === 200) {
            showMsg('detailMsg', `❌ Claim #${currentClaimId} has been <strong>REJECTED</strong>.`, 'success');
            document.getElementById('r-status').innerHTML = statusBadge(data.status);
            document.getElementById('r-updatedAt').textContent = formatDateTime(data.updatedAt);
            resetActionButtons(data.status);
        } else if (response.status === 400) {
            showMsg('detailMsg', '⚠️ Rejection reason is required.', 'error');
        } else {
            showMsg('detailMsg', `❌ Failed to reject claim: ${data.message || 'Unknown error.'}`, 'error');
        }

    } catch (err) {
        showMsg('detailMsg', '❌ Network error while rejecting claim.', 'error');
        console.error('Reject error:', err);
    } finally {
        btn.disabled = false;
        btn.textContent = '❌ Confirm Rejection';
    }
}

/* Auto-load pending claims when page opens */
window.addEventListener('DOMContentLoaded', loadPendingClaims);


/* =========================================================
   CLAIM HISTORY TAB
   ========================================================= */

// Cache all history claims for client-side filtering
let allHistoryClaims = [];

function showHistoryTab(btn) {
    showReviewerTab('historyTab', btn);
    if (allHistoryClaims.length === 0) {
        loadClaimHistory();
    }
}

async function loadClaimHistory() {
    hideMsg('historyMsg');
    const btn = document.getElementById('historyRefreshBtn');
    btn.disabled = true;
    btn.textContent = '⏳ Loading...';

    document.getElementById('historyTable').style.display = 'none';
    document.getElementById('historyEmpty').textContent = 'Loading claim history...';
    document.getElementById('historyEmpty').style.display = 'block';

    try {
        const response = await fetch(`${API}/claims/all`);
        const data = await response.json();

        if (response.status === 200) {
            allHistoryClaims = data;
            filterHistoryTable();
        } else {
            showMsg('historyMsg', '❌ Failed to load claim history.', 'error');
            document.getElementById('historyEmpty').textContent = 'Failed to load history.';
        }
    } catch (err) {
        showMsg('historyMsg', '❌ Network error. Make sure the server is running.', 'error');
        document.getElementById('historyEmpty').textContent = 'Network error.';
        console.error('History load error:', err);
    } finally {
        btn.disabled = false;
        btn.textContent = '🔄 Refresh';
    }
}

function filterHistoryTable() {
    const filter = document.getElementById('historyStatusFilter').value;
    const filtered = filter === 'ALL'
        ? allHistoryClaims
        : allHistoryClaims.filter(c => c.status === filter);
    renderHistoryTable(filtered);
}

function renderHistoryTable(claims) {
    const tbody = document.getElementById('historyTableBody');
    const table = document.getElementById('historyTable');
    const empty = document.getElementById('historyEmpty');
    const countEl = document.getElementById('historyCount');

    tbody.innerHTML = '';

    if (claims.length === 0) {
        table.style.display = 'none';
        empty.textContent = 'No claims found for the selected filter.';
        empty.style.display = 'block';
        countEl.textContent = '';
        return;
    }

    claims.forEach((claim, index) => {
        const tr = document.createElement('tr');
        tr.style.cssText = `
            cursor: pointer;
            border-bottom: 1px solid #e2eaf3;
            background: ${index % 2 === 0 ? '#fff' : '#f7fafd'};
            transition: background 0.15s;
        `;
        tr.onmouseover = () => tr.style.background = '#ddeeff';
        tr.onmouseout  = () => tr.style.background = index % 2 === 0 ? '#fff' : '#f7fafd';
        tr.onclick = () => openClaimForReview(claim.claimId);
        tr.title = 'Click to view full claim details';

        tr.innerHTML = `
            <td style="padding:10px 14px;font-weight:600;color:#1a3a5c;">#${claim.claimId}</td>
            <td style="padding:10px 14px;">${claim.customerName}</td>
            <td style="padding:10px 14px;font-family:monospace;color:#555;">${claim.policyNumber}</td>
            <td style="padding:10px 14px;">${claim.claimType}</td>
            <td style="padding:10px 14px;font-weight:600;">₹${Number(claim.claimAmount).toLocaleString('en-IN')}</td>
            <td style="padding:10px 14px;">${claim.destination}</td>
            <td style="padding:10px 14px;">${statusBadge(claim.status)}</td>
            <td style="padding:10px 14px;font-size:0.85rem;color:#666;">${formatDateTime(claim.createdAt)}</td>
        `;
        tbody.appendChild(tr);
    });

    table.style.display = 'table';
    empty.style.display = 'none';
    countEl.textContent = `Showing ${claims.length} of ${allHistoryClaims.length} claim(s)`;
}
