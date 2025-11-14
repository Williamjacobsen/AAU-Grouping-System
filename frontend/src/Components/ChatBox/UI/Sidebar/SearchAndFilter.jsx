export default function SearchAndFilter({ query, setQuery, filter, setFilter }) {
  return (
    <div
      style={{
        display: "flex",
        gap: "0.5rem",
        padding: "0.2rem 0.8rem",
				paddingBottom: "0",
        borderBottom: "1px solid #e5e7eb",
      }}
    >
      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search…"
        style={{
          flex: 1,
          padding: "0.5rem 1rem",
          border: "1px solid #e5e7eb",
          borderRadius: 4,
          outline: "none",
					height: "1.5rem",
					marginTop: "0.5rem"
        }}
      />
      <select
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
        style={{
          padding: "0.5rem 0.6rem",
          border: "1px solid #e5e7eb",
          borderRadius: 4,
          background: "white",
        }}
      >
        <option value="all">All</option>
        <option value="general">General</option>
        <option value="project">Projects</option>
        <option value="group">Groups</option>
        <option value="student">Students</option>
      </select>
    </div>
  );
}
