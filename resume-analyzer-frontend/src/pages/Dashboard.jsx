import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

const Dashboard = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-4xl mx-auto mt-10 p-6">

                {/* Welcome Card */}
                <div className="bg-white rounded-lg shadow p-8 mb-6">
                    <h2 className="text-3xl font-bold text-gray-800">
                        Welcome, {user?.fullName}! 👋
                    </h2>
                    <p className="text-gray-500 mt-2">
                        Analyze your resume and get AI-powered suggestions
                    </p>
                </div>

                {/* Action Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div
                        onClick={() => navigate('/upload')}
                        className="bg-blue-600 text-white rounded-lg shadow p-6 cursor-pointer hover:bg-blue-700 transition">
                        <div className="text-4xl mb-3">📄</div>
                        <h3 className="text-xl font-semibold">
                            Upload Resume
                        </h3>
                        <p className="text-blue-100 mt-1 text-sm">
                            Upload your PDF resume for analysis
                        </p>
                    </div>

                    <div className="bg-green-600 text-white rounded-lg shadow p-6">
                        <div className="text-4xl mb-3">📊</div>
                        <h3 className="text-xl font-semibold">
                            ATS Scoring
                        </h3>
                        <p className="text-green-100 mt-1 text-sm">
                            Get your ATS compatibility score
                        </p>
                    </div>

                    <div className="bg-purple-600 text-white rounded-lg shadow p-6">
                        <div className="text-4xl mb-3">🤖</div>
                        <h3 className="text-xl font-semibold">
                            AI Suggestions
                        </h3>
                        <p className="text-purple-100 mt-1 text-sm">
                            Get Gemini AI powered suggestions
                        </p>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default Dashboard;