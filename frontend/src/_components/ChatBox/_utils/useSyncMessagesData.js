import { useEffect } from "react";

/**
 * When a new messages is added to MessagesByRoom,
 * then it also needs to update lastActivityByRoom.
 * Otherwise, the sorting of chat rooms won't update live.
 *
 * Note: We intially call the backend for lastActivtyByRoom,
 * due to the fact that messagesByRoom,
 * only contains messages loaded by the client.
 */
export default function useSyncMessagesData(
  messagesByRoom,
  setLastActivityByRoom
) {
  useEffect(() => {
    if (!messagesByRoom) return;

    setLastActivityByRoom((prev) => {
      const lastActivityMap = { ...prev };

      for (const [roomKey, messages] of Object.entries(messagesByRoom)) {
        if (Array.isArray(messages) && messages.length > 0) {
          lastActivityMap[roomKey] = messages[messages.length - 1].timestamp;
        }
      }

			return lastActivityMap;
    });
  }, [messagesByRoom, setLastActivityByRoom]);
}
