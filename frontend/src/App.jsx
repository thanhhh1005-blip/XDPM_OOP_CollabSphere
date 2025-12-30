import { useState } from 'react';
import { generateMilestones, saveAiLog } from './services/aiService';

// Icon SVG đơn giản để trang trí
const SaveIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
  </svg>
);

const SparklesIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2 text-yellow-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 3.214L13 21l-2.286-6.857L5 12l5.714-3.214z" />
  </svg>
);

function App() {
  const [syllabus, setSyllabus] = useState('');
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saveStatus, setSaveStatus] = useState(null);
  const [error, setError] = useState('');

  const handleGenerate = async () => {
    if (!syllabus.trim()) {
        alert("Vui lòng nhập nội dung đề cương!");
        return;
    }
    setLoading(true);
    setError('');
    setPlan(null);
    setSaveStatus(null); 

    try {
      const response = await generateMilestones(syllabus);

      // Logic xử lý dữ liệu thông minh
      if (response && typeof response === 'object') {
          if (response.data) {
             try {
                const parsed = typeof response.data === 'string' ? JSON.parse(response.data) : response.data;
                setPlan(parsed);
             } catch(e) { setPlan(response); }
          } else {
             setPlan(response);
          }
      } else {
        setError("Dữ liệu trả về không đúng định dạng.");
      }

    } catch (err) {
      console.error(err);
      setError('Lỗi kết nối. Vui lòng kiểm tra lại Gateway.');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveToDB = async () => {
    if (!plan || !syllabus) return;
    try {
      await saveAiLog(syllabus, JSON.stringify(plan));
      setSaveStatus("SUCCESS");
    } catch (err) {
      setSaveStatus("ERROR");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 font-sans text-slate-800 pb-20">
      
      {/* HEADER */}
      <div className="bg-gradient-to-r from-blue-700 to-indigo-800 text-white py-12 shadow-xl mb-10">
        <div className="max-w-4xl mx-auto text-center px-4">
          <h1 className="text-5xl font-extrabold tracking-tight mb-4 flex items-center justify-center">
            <SparklesIcon /> CollabSphere AI
          </h1>
          <p className="text-blue-100 text-lg opacity-90 max-w-2xl mx-auto">
            Trợ lý lập kế hoạch dự án thông minh. Biến ý tưởng thô sơ thành lộ trình chi tiết chỉ trong vài giây.
          </p>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-6">
        
        {/* INPUT CARD */}
        <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-8 mb-10 transition-all hover:shadow-xl">
          <label className="block text-slate-700 font-bold text-lg mb-3">
            Mô tả ý tưởng dự án của bạn:
          </label>
          <textarea
            className="w-full p-4 border border-slate-200 rounded-xl bg-slate-50 focus:bg-white focus:ring-4 focus:ring-blue-100 focus:border-blue-500 outline-none h-40 transition-all text-slate-700 placeholder-slate-400"
            placeholder="Ví dụ: Xây dựng ứng dụng đặt lịch khám bệnh với React Native và Spring Boot, tích hợp thanh toán VNPAY, thời gian 3 tháng..."
            value={syllabus}
            onChange={(e) => setSyllabus(e.target.value)}
          />
          
          <div className="mt-6 flex justify-end">
            <button
                onClick={handleGenerate}
                disabled={loading}
                className={`flex items-center px-8 py-3 rounded-xl font-bold text-lg shadow-lg transform transition-all active:scale-95
                ${loading 
                    ? 'bg-slate-300 text-slate-500 cursor-not-allowed' 
                    : 'bg-gradient-to-r from-blue-600 to-indigo-600 text-white hover:from-blue-700 hover:to-indigo-700 hover:shadow-blue-200'}`}
            >
                {loading ? (
                    <>
                        <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Đang phân tích...
                    </>
                ) : (
                    <>🚀 Tạo Kế Hoạch AI</>
                )}
            </button>
          </div>
          
          {error && (
            <div className="mt-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 rounded-r">
                <span className="font-bold">Lỗi:</span> {error}
            </div>
          )}
        </div>

        {/* OUTPUT SECTION */}
        {plan && (
          <div className="animate-fade-in-up space-y-8">
            
            {/* ALERT SUCCESS/ERROR */}
            {saveStatus && (
                <div className={`p-4 rounded-xl text-center font-bold shadow-sm animate-bounce-short ${
                    saveStatus === 'SUCCESS' ? 'bg-green-100 text-green-800 border border-green-200' : 'bg-red-100 text-red-800 border border-red-200'
                }`}>
                    {saveStatus === 'SUCCESS' ? '✅ Đã lưu kế hoạch vào Database thành công!' : '❌ Lưu thất bại. Vui lòng thử lại.'}
                </div>
            )}

            {/* PROJECT HEADER CARD */}
            <div className="bg-white rounded-2xl shadow-lg border-l-8 border-blue-600 p-8 flex flex-col md:flex-row justify-between items-start gap-6">
                <div className="flex-1">
                    <h2 className="text-3xl font-extrabold text-slate-800 mb-3 leading-tight">
                        {plan.project_name || plan.projectName || "Dự án Mới"}
                    </h2>
                    <p className="text-slate-600 text-lg leading-relaxed">
                        {plan.overview || "Chưa có mô tả tổng quan."}
                    </p>
                </div>
                
                <button 
                    onClick={handleSaveToDB} 
                    className="shrink-0 bg-white border-2 border-green-500 text-green-600 hover:bg-green-50 font-bold py-2 px-6 rounded-lg shadow-sm flex items-center transition-colors"
                >
                    <SaveIcon /> Lưu Kết Quả
                </button>
            </div>

            {/* TIMELINE MILESTONES */}
            <div className="relative">
                {/* Line dọc nối các milestones */}
                <div className="absolute left-8 top-0 bottom-0 w-1 bg-slate-200 rounded hidden md:block"></div>

                <div className="space-y-8">
                    {(plan.milestones || []).map((milestone, index) => (
                        <div key={index} className="relative md:pl-24">
                            
                            {/* Circle Marker (Số thứ tự) */}
                            <div className="hidden md:flex absolute left-2 top-0 bg-blue-600 text-white w-12 h-12 rounded-full items-center justify-center font-bold text-xl shadow-lg ring-4 ring-white z-10">
                                {milestone.phase_number || index + 1}
                            </div>

                            {/* Content Card */}
                            <div className="bg-white rounded-xl shadow-md border border-slate-100 p-6 hover:shadow-lg transition-shadow">
                                <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-4 pb-4 border-b border-slate-100">
                                    <h3 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                                        <span className="md:hidden bg-blue-600 text-white w-8 h-8 rounded-full flex items-center justify-center text-sm">
                                            {milestone.phase_number || index + 1}
                                        </span>
                                        {milestone.phase_name || milestone.title}
                                    </h3>
                                    <span className="mt-2 sm:mt-0 bg-indigo-100 text-indigo-700 text-sm font-bold px-3 py-1 rounded-full border border-indigo-200 inline-block text-center">
                                        ⏱ {milestone.duration}
                                    </span>
                                </div>

                                <p className="text-slate-600 mb-5 italic bg-slate-50 p-3 rounded-lg border-l-4 border-indigo-300">
                                    "{milestone.description}"
                                </p>
                                
                                <div className="grid md:grid-cols-2 gap-6">
                                    {/* Tasks */}
                                    {milestone.tasks && (
                                        <div>
                                            <h4 className="font-bold text-xs text-slate-400 uppercase tracking-wider mb-3">Danh sách công việc</h4>
                                            <ul className="space-y-2">
                                                {milestone.tasks.map((task, i) => (
                                                    <li key={i} className="flex items-start text-sm text-slate-700 bg-slate-50 p-2 rounded border border-slate-100">
                                                        <span className="text-green-500 mr-2 font-bold">✓</span>
                                                        {task}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}

                                    {/* Deliverables */}
                                    {milestone.deliverables && (
                                        <div>
                                            <h4 className="font-bold text-xs text-slate-400 uppercase tracking-wider mb-3">Sản phẩm bàn giao</h4>
                                            <div className="bg-orange-50 border border-orange-100 text-orange-800 text-sm p-4 rounded-lg flex items-start gap-3">
                                                <span className="text-2xl">📦</span>
                                                <span className="font-medium mt-1">{milestone.deliverables}</span>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

          </div>
        )}
      </div>
    </div>
  );
}

export default App;