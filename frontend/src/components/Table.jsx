export default function Table({ columns, data, emptyMessage = 'No data available' }) {
  if (!data || data.length === 0) {
    return <p className="table-empty">{emptyMessage}</p>;
  }

  return (
    <div className="table-wrap">
      <table className="table-base">
        <thead>
          <tr>
            {columns.map(({ key, label }) => (
              <th key={key}>{label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={row.id ?? idx}>
              {columns.map(({ key, render }) => (
                <td key={key}>{render ? render(row[key], row) : row[key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
