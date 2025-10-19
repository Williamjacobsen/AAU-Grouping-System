const handleSendMessage = async (
  messageInput,
  selectedChatRoom,
  username,
  setMessageInput,
  chatSystem,
  setMessagesByRoom
) => {
  const content = messageInput.trim();
  if (!content || !selectedChatRoom) return;

  const isDirect = selectedChatRoom.startsWith("student"); // TODO
  const destination = isDirect
    ? "/private/send"
    : `/group/${selectedChatRoom}/send`;
  const payload = isDirect
    ? { content, sender: username, target: selectedChatRoom }
    : { content, sender: username };

  // no need to add the message to the messages state,
  // the client is subscribed to a websocket for this chat room,
  // so the subscription, adds it.
  setMessageInput("");

  try {
    await chatSystem.current.send(destination, payload);
  } catch (e) {
    console.error("Failed to send:", e);
    alert(
      "Failed to send your message. Please check your connection and try again."
    );
    setMessagesByRoom({});
  }
};

export default handleSendMessage;
