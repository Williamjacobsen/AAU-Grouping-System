import { useEffect } from "react";
import isDirectRoom from "./isDirectRoom";

function useFetchMessages(setMessagesByRoom, selectedChatRoom, username) {
  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const isDirect = isDirectRoom(selectedChatRoom); 
        const url = isDirect
          ? `http://localhost:8080/private/${username}/${selectedChatRoom}/messages/get/all`
          : `http://localhost:8080/group/${selectedChatRoom}/messages/get/all`;

        const response = await fetch(url);
        const data = await response.json();
        console.log(data);
        setMessagesByRoom((prev) => ({
          ...prev,
          [selectedChatRoom]: Array.isArray(data) ? data : [],
        }));
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
