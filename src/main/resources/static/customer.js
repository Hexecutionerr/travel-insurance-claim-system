/* =========================================================
   customer.js — Customer Portal JavaScript
   Handles: Claim Submission, Tab Navigation, Claim Tracking
   ========================================================= */

const API_BASE = '/api';

/* ---------- Tab Navigation ---------- */
function showTab(tabId, btn) {
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

/* ---------- Utility: Hide message ---------- */
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

/* ---------- Clear submit result ---------- */
function clearSubmitResult() {
    hideMsg('submitResult');
    document.getElementById('claimIdBox').style.display = 'none';
    clearAllFieldErrors();
}

/* =========================================================
   CLIENT-SIDE VALIDATION HELPERS
   ========================================================= */

/**
 * Show an inline error message below a specific field.
 * Creates a <span class="field-error"> element if it does not exist yet.
 */
function showFieldError(fieldId, message) {
    const input = document.getElementById(fieldId);
    if (!input) return;
    input.classList.add('input-error');
    let errEl = document.getElementById(fieldId + '-error');
    if (!errEl) {
        errEl = document.createElement('span');
        errEl.id = fieldId + '-error';
        errEl.className = 'field-error';
        input.parentNode.appendChild(errEl);
    }
    errEl.textContent = message;
    errEl.style.display = 'block';
}

/**
 * Remove the inline error for a specific field.
 */
function clearFieldError(fieldId) {
    const input = document.getElementById(fieldId);
    if (input) input.classList.remove('input-error');
    const errEl = document.getElementById(fieldId + '-error');
    if (errEl) errEl.style.display = 'none';
}

/**
 * Remove all inline field errors.
 */
function clearAllFieldErrors() {
    ['policyNumber','customerName','destination','travelDate',
     'claimType','claimAmount','description','documentReference']
        .forEach(clearFieldError);
}

/**
 * Run client-side validation on the claim submission form.
 * Returns true if valid, false otherwise (and shows per-field errors).
 *
 * NOTE: Backend validation is the final authority.
 * This is a convenience layer only — it does not replace server-side checks.
 */
function validateClaimForm(payload) {
    let valid = true;
    clearAllFieldErrors();

    if (!payload.customerName) {
        showFieldError('customerName', 'Customer name is required.');
        valid = false;
    }
    if (!payload.policyNumber) {
        showFieldError('policyNumber', 'Policy number is required.');
        valid = false;
    }
    if (!payload.destination) {
        showFieldError('destination', 'Destination is required.');
        valid = false;
    }
    if (!payload.travelDate) {
        showFieldError('travelDate', 'Travel date is required.');
        valid = false;
    }
    if (!payload.claimType) {
        showFieldError('claimType', 'Claim type is required.');
        valid = false;
    }
    if (isNaN(payload.claimAmount) || payload.claimAmount <= 0) {
        showFieldError('claimAmount', 'Claim amount must be greater than 0.');
        valid = false;
    }
    if (!payload.description) {
        showFieldError('description', 'Description is required.');
        valid = false;
    }
    if (!payload.documentReference) {
        showFieldError('documentReference', 'Document reference is required.');
        valid = false;
    }

    return valid;
}

/* =========================================================
   CLAIM SUBMISSION
   ========================================================= */
document.getElementById('claimForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    hideMsg('submitResult');
    document.getElementById('claimIdBox').style.display = 'none';

    const submitBtn = document.getElementById('submitBtn');

    // Build the request payload
    const payload = {
        policyNumber:      document.getElementById('policyNumber').value.trim(),
        customerName:      document.getElementById('customerName').value.trim(),
        destination:       document.getElementById('destination').value.trim(),
        travelDate:        document.getElementById('travelDate').value,
        claimType:         document.getElementById('claimType').value,
        claimAmount:       parseFloat(document.getElementById('claimAmount').value),
        description:       document.getElementById('description').value.trim(),
        documentReference: document.getElementById('documentReference').value.trim()
    };

    // --- Client-side validation (convenience layer) ---
    if (!validateClaimForm(payload)) {
        showMsg('submitResult', 'Please fix the highlighted fields before submitting.', 'error');
        return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';

    try {
        const response = await fetch(`${API_BASE}/claims`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.status === 201) {
            // Success — show the generated Claim ID
            clearAllFieldErrors();
            document.getElementById('generatedClaimId').textContent = data.claimId;
            document.getElementById('claimIdBox').style.display = 'block';
            document.getElementById('claimForm').reset();
            showMsg('submitResult', 'Claim submitted successfully! Your Claim ID is shown below.', 'success');
        } else if (response.status === 400) {
            // Backend validation error — display field errors returned by the server
            clearAllFieldErrors();
            let errorHtml = 'Please fix the following errors:<ul style="margin-top:6px;padding-left:18px;">';
            if (data.fieldErrors) {
                for (const [field, msg] of Object.entries(data.fieldErrors)) {
                    errorHtml += `<li><strong>${field}:</strong> ${msg}</li>`;
                    // Also highlight inline on the field if it exists in the form
                    showFieldError(field, msg);
                }
            } else {
                errorHtml += `<li>${data.message || 'Validation failed.'}</li>`;
            }
            errorHtml += '</ul>';
            showMsg('submitResult', errorHtml, 'error');
        } else {
            showMsg('submitResult', `Unexpected error: ${data.message || 'Please try again.'}`, 'error');
        }

    } catch (err) {
        showMsg('submitResult', 'Network error. Make sure the server is running.', 'error');
        console.error('Submit error:', err);
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Submit Claim';
    }
});


/* =========================================================
   CLAIM TRACKING
   ========================================================= */
async function trackClaim() {
    const claimIdInput = document.getElementById('trackClaimId');
    const claimId = claimIdInput.value.trim();

    hideMsg('trackResult');
    document.getElementById('claimDetailsCard').style.display = 'none';

    if (!claimId || isNaN(claimId) || Number(claimId) < 1) {
        showMsg('trackResult', 'Please enter a valid Claim ID.', 'error');
        return;
    }

    const trackBtn = document.getElementById('trackBtn');
    trackBtn.disabled = true;
    trackBtn.textContent = 'Searching...';

    try {
        const response = await fetch(`${API_BASE}/claims/${claimId}`);
        const data = await response.json();

        if (response.status === 200) {
            populateClaimDetails(data);
            document.getElementById('claimDetailsCard').style.display = 'block';
        } else if (response.status === 404) {
            showMsg('trackResult', `Claim with ID <strong>${claimId}</strong> was not found. Please check your Claim ID.`, 'error');
        } else {
            showMsg('trackResult', `Error: ${data.message || 'Unable to retrieve claim.'}`, 'error');
        }

    } catch (err) {
        showMsg('trackResult', 'Network error. Make sure the server is running.', 'error');
        console.error('Track error:', err);
    } finally {
        trackBtn.disabled = false;
        trackBtn.textContent = 'Search';
    }
}

/* ---------- Populate Claim Details Card ---------- */
function populateClaimDetails(claim) {
    document.getElementById('d-claimId').textContent          = claim.claimId;
    document.getElementById('d-status').innerHTML             = statusBadge(claim.status);
    document.getElementById('d-policyNumber').textContent     = claim.policyNumber;
    document.getElementById('d-customerName').textContent     = claim.customerName;
    document.getElementById('d-destination').textContent      = claim.destination;
    document.getElementById('d-travelDate').textContent       = claim.travelDate;
    document.getElementById('d-claimType').textContent        = claim.claimType;
    document.getElementById('d-claimAmount').textContent      = 'Rs. ' + Number(claim.claimAmount).toLocaleString('en-IN');
    document.getElementById('d-description').textContent      = claim.description;
    document.getElementById('d-documentReference').textContent= claim.documentReference;
    document.getElementById('d-createdAt').textContent        = formatDateTime(claim.createdAt);
    document.getElementById('d-updatedAt').textContent        = formatDateTime(claim.updatedAt);

    // Show rejection reason only if REJECTED
    const rejBox = document.getElementById('d-rejectionBox');
    if (claim.status === 'REJECTED' && claim.rejectionReason) {
        document.getElementById('d-rejectionReason').textContent = claim.rejectionReason;
        rejBox.style.display = 'block';
    } else {
        rejBox.style.display = 'none';
    }
}

/* Allow pressing Enter in the track input */
document.getElementById('trackClaimId').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') trackClaim();
});
