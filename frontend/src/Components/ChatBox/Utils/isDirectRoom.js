export default function isDirectRoom(selectedChatRoom, students) {
  if (!Array.isArray(students) || students.length === 0 || !selectedChatRoom) {
    return false;
  }

  for (const student of students) {
    if (student?.name === selectedChatRoom) {
      return true;
    }
  }

  return false;
}