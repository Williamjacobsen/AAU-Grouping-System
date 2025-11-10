export default function isDirectRoom(selectedChatRoom, students) {
  if (!Array.isArray(students) || students.length === 0) {
    return false;
  }

  for (const student of students) {
    if (student === selectedChatRoom) {
      return true;
    }
  }

  return false;
}