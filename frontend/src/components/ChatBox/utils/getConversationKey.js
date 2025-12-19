export default function getConversationKey(student1, student2) {
  return student1.localeCompare(student2) < 0 ? `${student1}-${student2}` : `${student2}-${student1}`;
}
