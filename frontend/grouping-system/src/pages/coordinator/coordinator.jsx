import React, { useEffect, useState } from "react";
import useGetSession from "./useGetSession";
import createSession from "./createSession";

export default function Coordinator() {
  const [name, setName] = useState("");
  const [sessions, setSessions] = useState([]);

  useGetSession(setSessions);

  const onSubmit = (event) => {
    event.preventDefault();
    if (!name.trim()) return;
    createSession(name);
    setSessions((prev) => [...prev, name]);
    setName("");
  };

  return (
    <div>
      <div>
        <h4>Sessions</h4>
        <div>
          {sessions.map((session) => (
            <p key={session}>{session}</p>
          ))}
        </div>
      </div>
      <br />
      <div>
        <form onSubmit={onSubmit}>
          <h4>Create Session:</h4>
          <input
            type="text"
            maxLength={50}
            placeholder="session name... "
            value={name}
            onChange={(event) => setName(event.target.value)}
          />
          <button type="submit">Create</button>
        </form>
      </div>
    </div>
  );
}
