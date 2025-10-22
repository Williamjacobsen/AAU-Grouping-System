import { useEffect, useRef, useState, useMemo } from "react";
import { useAppState } from "../../AppStateContext";
import ChatSystem from "./Utils/ChatSystem";
import useFetchMessages from "./Utils/useFetchMessages";
import handleSubscriptions from "./Utils/handleSubscriptions";
import HiddenChatBox from "./UI/HiddenChatBox";
import ChatArea from "./UI/ChatArea/ChatArea";
import Sidebar from "./UI/Sidebar";
import Header from "./UI/Header";
import sendReadReceipt from "./Utils/sendReadReceipt";

export default function ChatBox() {
  const [showChatBox, setShowChatBox] = useState(false);
  const [unreadMessagesByRoom, setUnreadMessagesByRoom] = useState({}); // TODO: Do for all: // { [room1]: int, [room2]: int }
  const [unreadMessagesCount, setUnreadMessagesCount] = useState(0);
  const [selectedChatRoom, setSelectedChatRoom] = useState(null);
  const [messageInput, setMessageInput] = useState("");
  const [messagesByRoom, setMessagesByRoom] = useState({});

  const chatSystem = useRef(null);
  const username = "My username";
  const { students, chatRooms } = useAppState();

  useFetchMessages(setMessagesByRoom, selectedChatRoom, username);

  useEffect(() => {
    chatSystem.current = new ChatSystem("http://localhost:8080/ws", username);

    chatSystem.current.connect(() => {
      handleSubscriptions(chatRooms, chatSystem, setMessagesByRoom, username);
    });

    return () => {
      if (chatSystem.current) {
        chatSystem.current.disconnect();
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const roomMessages = useMemo(
    () => messagesByRoom[selectedChatRoom] ?? [],
    [messagesByRoom, selectedChatRoom]
  );

  useEffect(() => {
    if (!selectedChatRoom) return;
    sendReadReceipt(selectedChatRoom, username, roomMessages, chatSystem);
  }, [selectedChatRoom, roomMessages, username]);

	async function getUnreadMessagesStatus() {
		await fetch(`http://localhost:8080/user/${username}/messages/unread/get/all`)
			.then((response) => response.json())
			.then((json) => {
				console.log(json);
				setUnreadMessagesByRoom(json ?? {});
				setUnreadMessagesCount(Object.values(json).reduce((a, n) => a + n, 0))
			})
			.catch((error) => console.error(error));
	}

	useEffect(() => {
		getUnreadMessagesStatus()
	}, [])

  return (
    <>
      {showChatBox ? (
        <div
          style={{
            border: "1px solid #e5e7eb",
            borderTopLeftRadius: "10px",
            borderTopRightRadius: "10px",
            position: "fixed",
            right: "5rem",
            bottom: 0,
            width: "60rem",
            height: "50rem",
            backgroundColor: "#f9fafb",
            boxShadow: "0 4px 16px rgba(0,0,0,0.15)",
            overflow: "hidden",
            display: "flex",
            flexDirection: "column",
          }}
        >
          <Header setShowChatBox={setShowChatBox} />
          <div
            style={{
              backgroundColor: "#f9fafb",
              width: "100%",
              flex: 1,
              height: "93%",
              position: "relative",
              display: "flex",
              overflow: "hidden",
            }}
          >
            <Sidebar
              chatRooms={chatRooms}
              selectedChatRoom={selectedChatRoom}
              setSelectedChatRoom={setSelectedChatRoom}
            />
            <ChatArea
              selectedChatRoom={selectedChatRoom}
              roomMessages={roomMessages}
              username={username}
              messageInput={messageInput}
              setMessageInput={setMessageInput}
              chatSystem={chatSystem}
              setMessagesByRoom={setMessagesByRoom}
              students={students}
            />
          </div>
        </div>
      ) : (
        <HiddenChatBox
          unreadMessagesCount={unreadMessagesCount}
          setShowChatBox={setShowChatBox}
        />
      )}
    </>
  );
}
