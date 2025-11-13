import React, { useState, memo } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

// TODO: make it so the button is green and cant be pressed after having send the emails (prevent email spam and provide user with feedback).

const NotifyButton = memo(({ sessionId, setMessage }) => {
  const [isSending, setIsSending] = useState(false);

  if (!sessionId) return null;

  async function sendNotifications() {
    try {
      setIsSending(true);
      await fetchWithDefaultErrorHandling(`/${sessionId}/notify`, {
        credentials: "include",
        method: "POST",
      });

      if (setMessage) setMessage("Notifications sent successfully!");
    } catch (error) {
      if (setMessage) setMessage(`Error sending notifications: ${error}`);
    } finally {
      setIsSending(false);
    }
  }

  return (
    <div className="checkbox-form">
      <div className="checkbox-group" style={{ display: "inline" }}>
        <label className="checkbox-label">
          When all groups have been made, send emails to students and
          supervisors:
        </label>
        <button
          className="button-primary"
          onClick={sendNotifications}
          disabled={isSending}
        >
          {isSending ? "Sending notifications..." : "Notify participants"}
        </button>
      </div>
    </div>
  );
});

export default NotifyButton;
