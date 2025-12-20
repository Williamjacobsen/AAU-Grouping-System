import { useEffect, useRef, useState, useMemo } from "react";
import { useAppState } from "../../context/AppStateContext";
import ChatSystem from "./utils/ChatSystem";
import useFetchMessages from "./utils/useFetchMessages";
import handleSubscriptions from "./utils/handleSubscriptions";
import HiddenChatBox from "./ui/HiddenChatBox";
import ChatArea from "./ui/chatArea/ChatArea";
import Sidebar from "./ui/sidebar/Sidebar";
import Header from "./ui/Header";
import sendReadReceipt from "./utils/sendReadReceipt";
import getUnreadMessagesCounters from "./utils/getUnreadMessagesCounters";
import getLastChatRoomActivityCounters from "./utils/getLastChatRoomActivityCounters";
import sortChatRooms from "./utils/sortChatRooms";
import useSyncMessagesData from "./utils/useSyncMessagesData";
import { useAuth } from "../../context/AuthProvider";
import isDirectRoom from "./utils/isDirectRoom";
import getConversationKey from "./utils/getConversationKey";

export default function ChatBox() {
	const [showChatBox, setShowChatBox] = useState(false);
	const [unreadMessagesByRoom, setUnreadMessagesByRoom] = useState({});
	const [unreadMessagesCount, setUnreadMessagesCount] = useState(0);
	const [lastActivityByRoom, setLastActivityByRoom] = useState({});
	const [selectedChatRoom, setSelectedChatRoom] = useState(null);
	const [messageInput, setMessageInput] = useState("");
	const [messagesByRoom, setMessagesByRoom] = useState({});

	const chatSystem = useRef(null);
	const { projects, groups, students, chatRooms } = useAppState();

	const { user } = useAuth();

	useFetchMessages(setMessagesByRoom, selectedChatRoom, user.name, students);

	useSyncMessagesData(messagesByRoom, setLastActivityByRoom);

	useEffect(() => {
		chatSystem.current = new ChatSystem(`${process.env.REACT_APP_API_BASE_URL}/ws`, user.name);

		chatSystem.current.connect(() => {
			handleSubscriptions(chatRooms, chatSystem, setMessagesByRoom, user.name, students);
		});

		return () => {
			if (chatSystem.current) {
				chatSystem.current.disconnect();
			}
		};
	}, [chatRooms]);

	const messageKey = useMemo(() => {
		if (!selectedChatRoom) return null;
		const isDirect = isDirectRoom(selectedChatRoom, students);
		return isDirect
			? getConversationKey(user.name, selectedChatRoom)
			: selectedChatRoom;
	}, [selectedChatRoom, students, user.name]);

	const roomMessages = useMemo(
		() => messagesByRoom[messageKey] ?? [],
		[messagesByRoom, messageKey]
	);

	useEffect(() => {
		if (!selectedChatRoom) return;
		sendReadReceipt(
			selectedChatRoom,
			user.name,
			roomMessages,
			chatSystem,
			students
		);

    if (messageKey) {
		  setUnreadMessagesByRoom((prev) => {
			  const updated = { ...prev };
			  updated[messageKey] = 0;
			  return updated;
		  });
	  }
	}, [selectedChatRoom, roomMessages, user.name, students]);

  useEffect(() => {
	  const total = Object.values(unreadMessagesByRoom).reduce((sum, count) => {
		  const numCount = Number(count);
	  	return sum + (isNaN(numCount) ? 0 : numCount);
	  }, 0);
	  setUnreadMessagesCount(total);
  }, [unreadMessagesByRoom]);

	useEffect(() => {
		getUnreadMessagesCounters(
			user.name,
			setUnreadMessagesByRoom,
			setUnreadMessagesCount
		);
		getLastChatRoomActivityCounters(user.name, setLastActivityByRoom);
	}, []);

	const orderedChatRooms = useMemo(
		() =>
			sortChatRooms(chatRooms, unreadMessagesByRoom, lastActivityByRoom, {
				projects,
				groups,
				students,
				currentUsername: user.name,
			}),
		[
			chatRooms,
			unreadMessagesByRoom,
			lastActivityByRoom,
			projects,
			groups,
			students,
			user.name,
		]
	);

	useEffect(() => {
		console.log(messagesByRoom);
	}, [messagesByRoom]);

	if (!user) return <div>Access denied: Not logged in.</div>;
	if (user.role !== "Student") {
		return;
	}

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
						height: "70vh",
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
							chatRooms={orderedChatRooms}
							selectedChatRoom={selectedChatRoom}
							setSelectedChatRoom={setSelectedChatRoom}
							unreadMessagesByRoom={unreadMessagesByRoom}
							setUnreadMessagesByRoom={setUnreadMessagesByRoom}
							projects={projects}
							groups={groups}
							students={students}
							username={user.name}
							setUnreadMessagesCount={setUnreadMessagesCount}
						/>
						<ChatArea
							selectedChatRoom={selectedChatRoom}
							roomMessages={roomMessages}
							username={user.name}
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
