import { useState } from "react";
import SearchAndFilter from "./SearchAndFilter";
import ChatRooms from "./ChatRooms";
import SearchAndFilterChatRooms from "../../Utils/SearchAndFilterChatRooms";

export default function Sidebar({
  chatRooms,
  selectedChatRoom,
  setSelectedChatRoom,
  unreadMessagesByRoom,
  projects,
  groups,
  students,
  setUnreadMessagesByRoom,
	username
}) {
  const [filter, setFilter] = useState("all"); // all | general | projects | groups | students
  const [query, setQuery] = useState("");

  const projectSet = new Set(projects.map((p) => p.name));
  const groupSet = new Set(groups.map((g) => g.name));
  const studentSet = new Set(students.map((s) => s.name));

  const filteredChatRooms = SearchAndFilterChatRooms(
    chatRooms,
    query,
    filter,
    projectSet,
    groupSet,
    studentSet
  );

  return (
    <div
      style={{
        backgroundColor: "white",
        position: "relative",
        width: "35rem",
        height: "100%",
        borderRight: "2px solid #e5e7eb",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <SearchAndFilter
        query={query}
        setQuery={setQuery}
        filter={filter}
        setFilter={setFilter}
      />
      <div style={{ flex: 1, overflowY: "auto" }}>
        <ChatRooms
          filteredChatRooms={filteredChatRooms}
          setSelectedChatRoom={setSelectedChatRoom}
          selectedChatRoom={selectedChatRoom}
          projectSet={projectSet}
          groupSet={groupSet}
          studentSet={studentSet}
          unreadMessagesByRoom={unreadMessagesByRoom}
          setUnreadMessagesByRoom={setUnreadMessagesByRoom}
					username={username}	
        />
      </div>
    </div>
  );
}
