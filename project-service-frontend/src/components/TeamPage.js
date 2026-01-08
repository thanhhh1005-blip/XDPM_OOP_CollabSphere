import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";

const API = "http://localhost:8088/api/v1";

export default function TeamPage() {
  // mock auth
  const [role, setRole] = useState("LECTURER");
  const [userId, setUserId] = useState("lec01");

  const headers = useMemo(
    () => ({
      "X-ROLE": role,
      "X-USER-ID": userId,
    }),
    [role, userId]
  );

  // data
  const [classId, setClassId] = useState("C01");
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [members, setMembers] = useState([]);
  const [checkpoints, setCheckpoints] = useState([]);

  // create team
  const [teamName, setTeamName] = useState("Team 1");

  // add member
  const [newMemberId, setNewMemberId] = useState("stu01");

  // create checkpoint
  const [cpTitle, setCpTitle] = useState("Checkpoint 1");
  const [cpDesc, setCpDesc] = useState("Nộp báo cáo tiến độ");
  const [cpAssignee, setCpAssignee] = useState("stu01");

  // submit checkpoint
  const [submitContent, setSubmitContent] = useState("Link drive: ...");

  const fetchTeams = async () => {
    const res = await axios.get(`${API}/teams`, { params: { classId } });
    setTeams(res.data);
  };

  const pickTeam = async (team) => {
    setSelectedTeam(team);

    const [mRes, cRes] = await Promise.all([
      axios.get(`${API}/teams/${team.id}/members`),
      axios.get(`${API}/teams/${team.id}/checkpoints`),
    ]);

    setMembers(mRes.data);
    setCheckpoints(cRes.data);
  };

  useEffect(() => {
    fetchTeams().catch(console.error);
    // eslint-disable-next-line
  }, []);

  const createTeam = async () => {
    await axios.post(
      `${API}/teams`,
      { classId, name: teamName },
      { headers }
    );
    await fetchTeams();
    alert("Tạo team OK");
  };

  const addMember = async () => {
    if (!selectedTeam) return alert("Chọn team trước");
    await axios.post(
      `${API}/teams/${selectedTeam.id}/members`,
      { userId: newMemberId },
      { headers }
    );
    await pickTeam(selectedTeam);
    alert("Thêm member OK");
  };

  const createCheckpoint = async () => {
    if (!selectedTeam) return alert("Chọn team trước");
    await axios.post(
      `${API}/teams/${selectedTeam.id}/checkpoints`,
      {
        title: cpTitle,
        description: cpDesc,
        assigneeId: cpAssignee || null,
      },
      { headers }
    );
    await pickTeam(selectedTeam);
    alert("Tạo checkpoint OK");
  };

  const submitCheckpoint = async (cpId) => {
    await axios.post(
      `${API}/checkpoints/${cpId}/submit`,
      { content: submitContent },
      { headers }
    );
    await pickTeam(selectedTeam);
    alert("Submit OK");
  };

  const markDone = async (cpId) => {
    await axios.post(`${API}/checkpoints/${cpId}/done`, null, { headers });
    await pickTeam(selectedTeam);
    alert("DONE OK");
  };

  const viewSubmissions = async (cpId) => {
    const res = await axios.get(`${API}/checkpoints/${cpId}/submissions`);
    alert(
      res.data.length
        ? res.data.map((x) => `${x.submitterId}: ${x.content}`).join("\n\n")
        : "Chưa có submission"
    );
  };

  return (
    <div style={{ padding: 20, maxWidth: 1100, margin: "0 auto" }}>
      <h1 style={{ marginTop: 0 }}>Team Service (Demo FE)</h1>

      {/* mock auth bar */}
      <div
        style={{
          display: "flex",
          gap: 12,
          alignItems: "center",
          padding: 12,
          border: "1px solid #e5e7eb",
          borderRadius: 10,
          marginBottom: 14,
        }}
      >
        <div style={{ fontWeight: 700 }}>Mock Auth:</div>
        <label>
          Role:&nbsp;
          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="LECTURER">LECTURER</option>
            <option value="STUDENT">STUDENT</option>
            <option value="HEAD_DEPARTMENT">HEAD_DEPARTMENT</option>
          </select>
        </label>

        <label>
          UserId:&nbsp;
          <input
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            style={{ width: 140 }}
          />
        </label>

        <div style={{ marginLeft: "auto", fontSize: 12, color: "#6b7280" }}>
          Headers gửi: X-ROLE / X-USER-ID
        </div>
      </div>

      {/* class + create team */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr auto",
          gap: 10,
          alignItems: "end",
          marginBottom: 16,
        }}
      >
        <label>
          ClassId
          <input
            value={classId}
            onChange={(e) => setClassId(e.target.value)}
            style={{ width: "100%" }}
          />
        </label>

        <label>
          Team name
          <input
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            style={{ width: "100%" }}
          />
        </label>

        <button onClick={createTeam} style={{ padding: "10px 12px" }}>
          + Tạo team
        </button>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "360px 1fr", gap: 16 }}>
        {/* teams list */}
        <div style={{ border: "1px solid #e5e7eb", borderRadius: 10 }}>
          <div style={{ padding: 12, borderBottom: "1px solid #eee", fontWeight: 800 }}>
            Teams (classId={classId})
          </div>
          <div style={{ padding: 10 }}>
            <button onClick={fetchTeams} style={{ marginBottom: 10 }}>
              Refresh
            </button>
            {teams.map((t) => (
              <div
                key={t.id}
                onClick={() => pickTeam(t)}
                style={{
                  padding: 10,
                  borderRadius: 8,
                  border: "1px solid #eee",
                  marginBottom: 8,
                  cursor: "pointer",
                  background: selectedTeam?.id === t.id ? "#f3f4f6" : "#fff",
                }}
              >
                <div style={{ fontWeight: 800 }}>{t.name}</div>
                <div style={{ fontSize: 12, color: "#6b7280" }}>
                  teamId: {t.id}
                </div>
                <div style={{ fontSize: 12, color: "#6b7280" }}>
                  leaderId: {t.leaderId || "-"}
                </div>
              </div>
            ))}
            {!teams.length && <div style={{ color: "#6b7280" }}>Chưa có team</div>}
          </div>
        </div>

        {/* detail */}
        <div style={{ border: "1px solid #e5e7eb", borderRadius: 10 }}>
          <div style={{ padding: 12, borderBottom: "1px solid #eee", fontWeight: 800 }}>
            Team detail
          </div>

          {!selectedTeam ? (
            <div style={{ padding: 14, color: "#6b7280" }}>
              Chọn 1 team để xem chi tiết
            </div>
          ) : (
            <div style={{ padding: 14 }}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <div>
                  <div style={{ fontWeight: 900, fontSize: 18 }}>{selectedTeam.name}</div>
                  <div style={{ fontSize: 12, color: "#6b7280" }}>
                    teamId: {selectedTeam.id}
                  </div>
                </div>
                <button onClick={() => pickTeam(selectedTeam)}>Reload</button>
              </div>

              {/* members */}
              <div style={{ marginTop: 16 }}>
                <div style={{ fontWeight: 800, marginBottom: 8 }}>Members</div>
                <div style={{ display: "flex", gap: 8, marginBottom: 10 }}>
                  <input
                    value={newMemberId}
                    onChange={(e) => setNewMemberId(e.target.value)}
                    placeholder="stu01"
                  />
                  <button onClick={addMember}>+ Add</button>
                </div>
                <ul style={{ marginTop: 0 }}>
                  {members.map((m) => (
                    <li key={m.id}>
                      {m.userId} — <b>{m.roleInTeam}</b>
                    </li>
                  ))}
                </ul>
              </div>

              {/* checkpoints */}
              <div style={{ marginTop: 16 }}>
                <div style={{ fontWeight: 800, marginBottom: 8 }}>Checkpoints</div>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr 1fr auto",
                    gap: 8,
                    marginBottom: 10,
                  }}
                >
                  <input value={cpTitle} onChange={(e) => setCpTitle(e.target.value)} placeholder="title" />
                  <input value={cpDesc} onChange={(e) => setCpDesc(e.target.value)} placeholder="desc" />
                  <input value={cpAssignee} onChange={(e) => setCpAssignee(e.target.value)} placeholder="assigneeId" />
                  <button onClick={createCheckpoint}>+ Create</button>
                </div>

                <div style={{ marginBottom: 10 }}>
                  <textarea
                    value={submitContent}
                    onChange={(e) => setSubmitContent(e.target.value)}
                    rows={2}
                    style={{ width: "100%" }}
                    placeholder="Nội dung submit..."
                  />
                </div>

                <table style={{ width: "100%", borderCollapse: "collapse" }} border="1">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Status</th>
                      <th>Assignee</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {checkpoints.map((cp) => (
                      <tr key={cp.id}>
                        <td style={{ padding: 6 }}>{cp.title}</td>
                        <td style={{ padding: 6 }}>{cp.status}</td>
                        <td style={{ padding: 6 }}>{cp.assigneeId || "-"}</td>
                        <td style={{ padding: 6 }}>
                          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                            <button onClick={() => submitCheckpoint(cp.id)}>Submit</button>
                            <button onClick={() => markDone(cp.id)}>DONE</button>
                            <button onClick={() => viewSubmissions(cp.id)}>Submissions</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                    {!checkpoints.length && (
                      <tr>
                        <td colSpan="4" style={{ padding: 10, color: "#6b7280" }}>
                          Chưa có checkpoint
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>

                <div style={{ fontSize: 12, color: "#6b7280", marginTop: 8 }}>
                  Lưu ý: Submit sẽ bị chặn nếu bạn không thuộc team hoặc không đúng assignee (nếu có).
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
