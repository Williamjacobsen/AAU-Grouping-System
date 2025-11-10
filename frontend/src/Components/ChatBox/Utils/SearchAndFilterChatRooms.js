export default function SearchAndFilterChatRooms(
  chatRooms,
  query,
  filter,
  projectSet,
  groupSet,
  studentSet
) {
  const typeOf = (room) =>
    room === "General"
      ? "general"
      : projectSet.has(room)
      ? "project"
      : groupSet.has(room)
      ? "group"
      : studentSet.has(room)
      ? "student"
      : "";

  return chatRooms
    .filter((room) => room.toLowerCase().includes(query.toLowerCase()))
    .filter((room) => (filter === "all" ? true : typeOf(room) === filter));
}
