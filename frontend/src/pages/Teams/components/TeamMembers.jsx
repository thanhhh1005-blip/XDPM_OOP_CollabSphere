import React, { useMemo, useState } from 'react';
import axios from 'axios';
import { getAuthInfo } from '../../../utils/authStorage';

const TeamMembers = ({ team, reload }) => {
  const auth = getAuthInfo() || {};
  const { role, userId } = auth;

  const isLecturer = role === 'LECTURER';
  const isLeader = String(team.leaderId) === String(userId);

  const [newMemberId, setNewMemberId] = useState('');

  const headers = useMemo(() => ({
    'X-ROLE': role,
    'X-USER-ID': userId,
    Authorization: auth.token ? `Bearer ${auth.token}` : undefined
  }), [role, userId, auth.token]);

  const API = `http://localhost:8080/api/v1/teams/${team.id}`;

  const addMember = () => {
    if (!newMemberId) return alert('Nhập userId');

    axios.post(
      `${API}/add-member`,
      { userId: newMemberId },
      { headers }
    ).then(() => {
      setNewMemberId('');
      reload();
    }).catch(err =>
      alert(err.response?.data?.message || err.message)
    );
  };

  const removeMember = (memberId) => {
    if (!window.confirm('Xóa thành viên này?')) return;

    axios.post(
      `${API}/remove-member`,
      { userId: memberId },
      { headers }
    ).then(reload)
     .catch(err =>
      alert(err.response?.data?.message || err.message)
     );
  };

  return (
    <div style={{ marginTop: 24 }}>
      <h3>👥 Thành viên Team</h3>

      <ul style={{ paddingLeft: 20 }}>
        {team.members.map(m => (
          <li key={m.userId} style={{ marginBottom: 6 }}>
            <b>{m.fullName}</b> — {m.role}

            {(isLecturer || isLeader) && m.userId !== team.leaderId && (
              <button
                style={{ marginLeft: 12, color: 'red' }}
                onClick={() => removeMember(m.userId)}
              >
                Xóa
              </button>
            )}
          </li>
        ))}
      </ul>

      {(isLecturer || isLeader) && (
        <div style={{ marginTop: 12 }}>
          <input
            placeholder="User ID sinh viên"
            value={newMemberId}
            onChange={e => setNewMemberId(e.target.value)}
          />
          <button onClick={addMember} style={{ marginLeft: 8 }}>
            + Thêm
          </button>
        </div>
      )}
    </div>
  );
};

export default TeamMembers;
