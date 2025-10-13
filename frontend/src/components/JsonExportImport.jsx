import React, { useRef } from "react";

/**
 * JsonExportImport
 * Props:
 * - getData: () => any  (a function that returns the data to export)
 * - setData: (data) => void  (a function to replace data when importing)
 * - filenamePrefix: optional string prefix for export filename
 */
export default function JsonExportImport({ getData, setData, filenamePrefix = 'export' }) {
  const fileInputRef = useRef(null);

  function downloadJsonFile(filename, obj) {
    const json = JSON.stringify(obj, null, 2);
    const blob = new Blob([json], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  function handleExport() {
    const data = getData && getData();
    if (!data) {
      alert('No data available to export.');
      return;
    }
    const name = `${filenamePrefix}-${Date.now()}.json`;
    downloadJsonFile(name, data);
  }

  function handleImportClick() {
    if (fileInputRef.current) fileInputRef.current.value = null;
    if (fileInputRef.current) fileInputRef.current.click();
  }

  function handleFileChange(e) {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (ev) => {
      try {
        const parsed = JSON.parse(ev.target.result);
        // Try common shapes: array, {students: [...]}, {session: {students: [...]}}
        let payload = null;
        if (Array.isArray(parsed)) payload = parsed;
        else if (Array.isArray(parsed.students)) payload = parsed.students;
        else if (parsed.session && Array.isArray(parsed.session.students)) payload = parsed.session.students;
        else payload = parsed; // fallback: give caller the raw parsed object

        if (!payload) {
          alert('Imported JSON did not contain usable data.');
          return;
        }

        if (setData) setData(payload);
        alert('Import successful.');
      } catch (err) {
        alert('Failed to parse JSON: ' + err.message);
      }
    };
    reader.readAsText(file);
  }

  return (
    <div style={{ marginBottom: 12 }}>
      <button onClick={handleExport} style={{ marginRight: 8 }}>Export JSON</button>
      <button onClick={handleImportClick}>Import JSON</button>
      <input ref={fileInputRef} type="file" accept="application/json" style={{ display: 'none' }} onChange={handleFileChange} />
    </div>
  );
}
