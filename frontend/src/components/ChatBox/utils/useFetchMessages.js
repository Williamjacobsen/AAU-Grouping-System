import { useEffect } from "react";
import isDirectRoom from "./isDirectRoom";
import getConversationKey from "./getConversationKey";

function useFetchMessages(
  setMessagesByRoom,
  selectedChatRoom,
  username,
  students
) {
  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const isDirect = isDirectRoom(selectedChatRoom, students);

        if (isDirect) {
          const url = `${process.env.REACT_APP_API_BASE_URL}/api/private/${username}/${selectedChatRoom}/messages/get/all`;

          const response = await fetch(url);
          const data = await response.json();

          const conversationKey = getConversationKey(
            username,
            selectedChatRoom
          );

          setMessagesByRoom((prev) => ({
            ...prev,
            [conversationKey]: Array.isArray(data) ? data : [],
          }));
        } else {
          const url = `${process.env.REACT_APP_API_BASE_URL}/api/group/${selectedChatRoom}/messages/get/all`;

          const response = await fetch(url);
          const data = await response.json();

          setMessagesByRoom((prev) => ({
            ...prev,
            [selectedChatRoom]: Array.isArray(data) ? data : [],
          }));
        }
      } catch (error) {
        console.error("Error fetching messages:", error);
      }
    };

    if (selectedChatRoom) {
      fetchMessages();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedChatRoom]);
}

export default useFetchMessages;
