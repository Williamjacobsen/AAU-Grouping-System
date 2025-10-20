import React, { useMemo, useState } from "react";

// Hook that provides a set of selectable student columns and a small UI to toggle them
export default function useStudentColumns() {

  // Define all available columns and their accessors (defensive about missing fields)
  const allColumns = useMemo(() => ([
    { label: "Name", accessor: s => s?.name ?? "" },
    { label: "Group", accessor: s => s?.group?.number ?? "" },
    { label: "Group project", accessor: s => s?.group?.project ?? "" },
    { label: "Desired working environment", accessor: s => s?.desiredWorkingEnvironment ?? s?.desiredWorkingEnvironments ?? s?.workingEnvironment ?? "" },
    { label: "Personal skills", accessor: s => {
      const skills = s?.personalSkills ?? s?.skills ?? s?.skillList ?? s?.skillsList;
      if (!skills) return "";
      return Array.isArray(skills) ? skills.join(", ") : String(skills);
    } },
  ]), []);

  // By default show the first three columns (Name, Group, Group project)
  const [selectedLabels, setSelectedLabels] = useState(new Set([
    "Name",
    "Group",
    "Group project"
  ]));

  const toggleLabel = (label) => {
    setSelectedLabels(prev => {
      const next = new Set(prev);
      if (next.has(label)) next.delete(label); else next.add(label);
      return next;
    });
  };

  const selectedColumns = useMemo(() => {
    return allColumns.filter(col => selectedLabels.has(col.label));
  }, [allColumns, selectedLabels]);

  // Small UI component that lists checkboxes to select visible columns
  function ColumnsSelector() {
    return (
      <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
        {allColumns.map(col => (
          <label key={col.label} style={{ display: 'inline-flex', gap: '0.25rem', alignItems: 'center' }}>
            <input
              type="checkbox"
              checked={selectedLabels.has(col.label)}
              onChange={() => toggleLabel(col.label)}
            />
            <span>{col.label}</span>
          </label>
        ))}
      </div>
    );
  }

  return { selectedColumns, ColumnsSelector };
}
