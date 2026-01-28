import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import axios from 'axios';

// Bỏ props 'teams' vì không cần dùng để ghép tên nữa
const ScheduleCalendar = ({ classId, onEventClick }) => { 
  const [events, setEvents] = useState([]);

  useEffect(() => {
    const fetchSchedules = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/schedules/class/${classId}`);
        const rawData = response.data.result || response.data || [];

        // 👇 LOGIC ĐƠN GIẢN: Backend trả sao hiển thị vậy
        const processedEvents = rawData.map(evt => ({
            id: evt.id,
            title: evt.title, // Backend đã ghép sẵn "Team A - ..." rồi, dùng luôn!
            start: evt.startTime,
            end: evt.endTime,
            extendedProps: {
                ...evt,
                // Lưu rawTitle nếu muốn (để lúc edit form cắt bớt tên team đi cho đẹp)
                // Nếu backend trả về title đã ghép, ta có thể xử lý cắt chuỗi ở form sau
                rawTitle: evt.title 
            }
        }));

        setEvents(processedEvents);
      } catch (error) {
        console.error("Lỗi tải lịch:", error);
      }
    };

    if (classId) fetchSchedules();
  }, [classId]); // Bỏ dependency 'teams'

  const renderEventContent = (eventInfo) => {
    const isTeamEvent = eventInfo.event.extendedProps.type === 'TEAM';

    // Màu sắc (giữ nguyên logic cũ cho đẹp)
    const backgroundColor = isTeamEvent ? '#e6f7ff' : '#f6ffed'; // Xanh dương nhạt / Xanh lá nhạt
    const borderColor = isTeamEvent ? '#91d5ff' : '#b7eb8f';     // Viền xanh đậm hơn

    return (
      <div
        style={{
          // 👇 QUAN TRỌNG NHẤT: Kéo dài hết chiều ngang
          width: '100%',
          boxSizing: 'border-box', // Đảm bảo padding không làm vỡ khung
          margin: '1px 0', // Chỉ để margin trên dưới nhỏ để tách các sự kiện, bỏ margin trái phải

          // Style khung và màu sắc
          padding: '2px 4px',             // Padding nhỏ gọn bên trong
          borderRadius: '4px',            // Bo góc tròn như bạn muốn
          backgroundColor: backgroundColor, // Màu nền
          border: `1px solid ${borderColor}`, // Viền màu xanh/lá
          color: '#000',                  // Chữ màu đen dễ đọc
          cursor: 'pointer',
          overflow: 'hidden',
          display: 'flex',                // Dùng flex để căn chỉnh nội dung
          flexDirection: 'column',        // Xếp chồng lên nhau
        }}
      >
        {/* Dòng 1: Giờ + Tiêu đề (Nằm cùng 1 hàng ngang) */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', overflow: 'hidden' }}>
             {/* Giờ */}
             <span style={{ fontWeight: 'bold', fontSize: '11px', whiteSpace: 'nowrap' }}>
                {eventInfo.timeText}
             </span>
             {/* Tiêu đề (tự động cắt nếu quá dài) */}
             <span style={{ fontSize: '12px', fontWeight: '500', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} title={eventInfo.event.title}>
                {eventInfo.event.title}
             </span>
        </div>

        {/* Dòng 2: Địa điểm (nếu có) */}
        {eventInfo.event.extendedProps.location && (
            <div style={{ fontSize: '10px', opacity: 0.7, marginTop: '1px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                📍 {eventInfo.event.extendedProps.location}
            </div>
        )}
      </div>
    );
  };

  return (
    <div className="calendar-container h-full" style={{ padding: '10px' }}> {/* Thêm padding cho container */}
      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek'
        }}
        events={events}
        eventContent={renderEventContent}
        eventClick={onEventClick}
        height="75vh"
        // Thêm một số style cho FullCalendar để bỏ các viền mặc định không cần thiết
        eventBorderColor="transparent"
        eventBackgroundColor="transparent"
      />
    </div>
  );
};

export default ScheduleCalendar;