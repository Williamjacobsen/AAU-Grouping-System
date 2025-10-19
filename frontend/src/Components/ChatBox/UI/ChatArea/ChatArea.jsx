import InputField from "./InputField";
import ChatMessagesArea from "./ChatMessagesArea";
import ChatRoomHeader from "./ChatRoomHeader";
import NoChatRoomSelected from "./NoChatRoomSelected";
import handleSendMessage from "../../Utils/handleSendMessage";

export default function ChatArea({
  selectedChatRoom,
  roomMessages,
  username,
  messageInput,
  setMessageInput,
	chatSystem,
	setMessagesByRoom,
	students
}) {
  return (
    <div
      style={{
        width: "100%",
        flexDirection: "column",
      }}
    >
      {selectedChatRoom ? (
        <>
          <ChatRoomHeader selectedChatRoom={selectedChatRoom} />
          <ChatMessagesArea roomMessages={roomMessages} username={username} />
          <InputField
            messageInput={messageInput}
            setMessageInput={setMessageInput}
            handleSendMessage={handleSendMessage}
            selectedChatRoom={selectedChatRoom}
            username={username}
            chatSystem={chatSystem}
            setMessagesByRoom={setMessagesByRoom}
						students={students}
          />
        </>
      ) : (
        <NoChatRoomSelected />
      )}
    </div>
  );
}
