import getConversationKey from "./getConversationKey";
import isDirectRoom from "./isDirectRoom";

function handleSubscriptions(
  chatRooms,
  chatSystem,
  setMessagesByRoom,
  username,
  students 
) {
  chatRooms.forEach((roomName) => {
    const isDirect = isDirectRoom(roomName, students);

    if (isDirect) {
      console.log(`Skipping group subscription for direct room: ${roomName}`);
      return;
    }

    chatSystem.current.subscribe(`/group/${roomName}/messages`, (message) => {
      console.log(`Received message in group ${roomName}:`, message);

      setMessagesByRoom((prev) => {
        const prevMessages = prev[roomName] ?? [];
        return {
          ...prev,
          [roomName]: [...prevMessages, message],
        };
      });
    });
  });

  chatSystem.current.subscribe("/user/private/reply", (message) => {
    console.log("Private message:", message);

    const conversationKey = getConversationKey(message.sender, message.target);

    setMessagesByRoom((prev) => {
      const prevMessages = prev[conversationKey] ?? [];
      return { ...prev, [conversationKey]: [...prevMessages, message] };
    });
  });
}

export default handleSubscriptions;
