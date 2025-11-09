// Sort order:
// 1) Most recent activity (newest first)
// 2) Rooms with unread messages
// 3) Category: General > Projects > Groups > Students
// 4) Alphabetical by name
export default function sortChatRooms(
  chatRooms,
  unreadByRoom = {},
  lastActivityByRoom = {},
  { projects = [], groups = [], students = [] } = {}
) {
  const projectSet = new Set(projects);
  const groupSet = new Set(groups);
  const studentSet = new Set(students);

  function getCategoryRank(name) {
    if (name === "General") return 0;       // special case
    if (projectSet.has(name)) return 1;     // projects
    if (groupSet.has(name)) return 2;       // groups
    if (studentSet.has(name)) return 3;     // students
  }

  const rows = chatRooms.map((name) => ({
    name,
    lastActive: Number(lastActivityByRoom?.[name] ?? 0),
    hasUnread: Number(unreadByRoom?.[name] ?? 0) > 0,
    category: getCategoryRank(name),
  }));

  rows.sort((a, b) => {
    if (b.lastActive !== a.lastActive) return b.lastActive - a.lastActive; 
    if (b.hasUnread !== a.hasUnread) return (b.hasUnread ? 1 : 0) - (a.hasUnread ? 1 : 0);
    if (a.category !== b.category) return a.category - b.category;
    return a.name.localeCompare(b.name, undefined, { numeric: true, sensitivity: "base" });
  });

  return rows.map((r) => r.name);
}