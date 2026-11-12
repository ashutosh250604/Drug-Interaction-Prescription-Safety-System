import React, { useMemo } from 'react';

const SEVERITY_ORDER = { HIGH: 0, MEDIUM: 1, LOW: 2 };

const SEVERITY_CLASS = {
  HIGH: 'sev-high',
  MEDIUM: 'sev-medium',
  LOW: 'sev-low',
};

const severityClass = (severity) => SEVERITY_CLASS[severity] || 'sev-unknown';

function InteractionResult({ interactions = [], checkedDrugs = [] }) {
  const sorted = useMemo(
    () =>
      [...interactions].sort(
        (a, b) =>
          (SEVERITY_ORDER[a.severity] ?? 3) - (SEVERITY_ORDER[b.severity] ?? 3) ||
          a.drug1.localeCompare(b.drug1)
      ),
    [interactions]
  );

  if (sorted.length === 0) {
    return (
      <div className="empty-findings">
        <h3>No known interaction between these {checkedDrugs.length} medications</h3>
        <p>
          The database holds no pair for this exact combination. That is not proof the
          regimen is safe: unlisted pairs, doses, timing, renal function and the clinical
          picture all sit outside this tool.
        </p>
      </div>
    );
  }

  return (
    <ol className="findings">
      {sorted.map((interaction, index) => (
        <li
          key={interaction.id ?? `${interaction.drug1}-${interaction.drug2}-${index}`}
          className={`interaction-card ${severityClass(interaction.severity)}`}
        >
          <div className="card-head">
            <h3 className="pair">
              {interaction.drug1}
              <span className="pair-plus"> + </span>
              {interaction.drug2}
            </h3>
            <span className={`severity-tag ${severityClass(interaction.severity)}`}>
              <span className="dot" aria-hidden="true" />
              {interaction.severity}
            </span>
          </div>

          <p className="card-summary">{interaction.description}</p>

          <div className="card-details">
            <div className="detail">
              <span className="detail-label">Why it happens</span>
              <p>{interaction.mechanism || 'Mechanism not recorded.'}</p>
            </div>
            <div className="detail">
              <span className="detail-label">What to do</span>
              <p>{interaction.management || 'Review the source literature.'}</p>
            </div>
          </div>

          <div className="card-foot">
            <span className="evidence">{interaction.evidence || 'Unknown'} evidence</span>
          </div>
        </li>
      ))}
    </ol>
  );
}

export default InteractionResult;
