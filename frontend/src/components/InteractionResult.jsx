import React from 'react';

function InteractionResult({ interactions }) {
  const getSeverityClass = (severity) => {
    switch (severity) {
      case 'HIGH':
        return 'severity-high';
      case 'MEDIUM':
        return 'severity-medium';
      case 'LOW':
        return 'severity-low';
      default:
        return '';
    }
  };

  const getSeverityIcon = (severity) => {
    switch (severity) {
      case 'HIGH':
        return '⚠️';
      case 'MEDIUM':
        return '⚡';
      case 'LOW':
        return 'ℹ️';
      default:
        return '❓';
    }
  };

  const getSeverityMessage = (severity) => {
    switch (severity) {
      case 'HIGH':
        return 'Severe Interaction - Avoid Combination';
      case 'MEDIUM':
        return 'Moderate Interaction - Use Caution';
      case 'LOW':
        return 'Minor Interaction - Monitor';
      default:
        return 'Unknown Severity';
    }
  };

  if (!interactions || interactions.length === 0) {
    return (
      <div className="interaction-result no-interactions">
        <div className="success-icon">✓</div>
        <h3>No Interactions Found</h3>
        <p>The selected drugs have no known interactions in our database.</p>
        <p className="note">Always consult with a healthcare professional.</p>
      </div>
    );
  }

  return (
    <div className="interaction-result">
      <h3 className="result-header">
        Found {interactions.length} Interaction{interactions.length > 1 ? 's' : ''}
      </h3>
      <div className="interactions-list">
        {interactions.map((interaction, index) => (
          <div
            key={index}
            className={`interaction-card ${getSeverityClass(interaction.severity)}`}
          >
            <div className="interaction-header">
              <span className="severity-icon">{getSeverityIcon(interaction.severity)}</span>
              <span className="drug-pair">
                {interaction.drug1} + {interaction.drug2}
              </span>
            </div>
            <div className="severity-badge">
              <span className={`severity-label ${getSeverityClass(interaction.severity)}`}>
                {interaction.severity}
              </span>
              <span className="severity-message">{getSeverityMessage(interaction.severity)}</span>
            </div>
            <p className="interaction-description">{interaction.description}</p>
          </div>
        ))}
      </div>
      <div className="disclaimer">
        <p>
          <strong>Disclaimer:</strong> This information is for reference only. Always consult with a healthcare professional before making any medication decisions.
        </p>
      </div>
    </div>
  );
}

export default InteractionResult;
