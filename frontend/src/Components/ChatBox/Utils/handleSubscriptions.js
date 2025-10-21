function handleSubscriptions(
  chatRooms,
  chatSystem,
  setMessagesByRoom,
  username,
) {
  chatRooms.forEach((roomName) => { // TODO: chatRoom include students, which it shouldn't.
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

    const conversationKey =
      message.sender === username ? message.target : message.sender;
    setMessagesByRoom((prev) => {
      const prevMessages = prev[conversationKey] ?? [];
      return { ...prev, [conversationKey]: [...prevMessages, message] };
    });
  });
}

export default handleSubscriptions;
