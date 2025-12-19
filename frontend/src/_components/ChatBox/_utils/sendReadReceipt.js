import isDirectRoom from "./isDirectRoom";
import getConversationKey from "./getConversationKey";

export default function sendReadReceipt(
  selectedChatRoom,
  username,
  roomMessages,
  chatSystemRef,
	students
) {
  if (!selectedChatRoom || !roomMessages?.length || !chatSystemRef.current)
    return;

  const upToMessageId = roomMessages[roomMessages.length - 1].id;

  if (isDirectRoom(selectedChatRoom, students)) {
    const conversationKey = getConversationKey(username, selectedChatRoom);
    chatSystemRef.current.publish("/private/readUpTo", {
      conversationKey,
      username,
      upToMessageId,
    });
  } else {
    chatSystemRef.current.publish(`/group/${selectedChatRoom}/readUpTo`, {
      username,
      upToMessageId,
    });
  }
}
