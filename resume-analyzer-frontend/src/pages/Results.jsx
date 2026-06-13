import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    RadialBarChart, RadialBar, ResponsiveContainer,
    BarChart, Bar, XAxis, YAxis, CartesianGrid,
    Tooltip, Legend, PieChart, Pie, Cell
} from 'recharts';
import { resumeApi, aiApi } from '../services/api';
import Navbar from '../components/Navbar';

const COLORS = ['#0088FE', '#FF8042'];

const Results = () => {
    const { resumeId } = useParams();
    const navigate = useNavigate();
    const [analysis, setAnalysis] = useState(null);
    const [aiSuggestion, setAiSuggestion] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchResults();
    }, [resumeId]);

    const fetchResults = async () => {
        try {
            setLoading(true);

            // Poll until analysis is COMPLETED
            let analysisData = null;
            let attempts = 0;

            while (attempts < 10) {
                try {
                    const response = await resumeApi.getAnalysis(resumeId);
                    if (response.data.status === 'COMPLETED') {
                        analysisData = response.data;
                        break;
                    }
                } catch (e) {
                    // Analysis not ready yet
                }
                await new Promise(r => setTimeout(r, 2000));
                attempts++;
            }

            if (analysisData) {
                setAnalysis(analysisData);

                // Try to get AI suggestion
                try {
                    await new Promise(r => setTimeout(r, 3000));
                    const aiResponse = await aiApi.getSuggestion(resumeId);
                    setAiSuggestion(aiResponse.data);
                } catch (e) {
                    console.log('AI suggestion not ready yet');
                }
            } else {
                setError('Analysis is taking longer than expected');
            }

        } catch (err) {
            setError('Failed to fetch results');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100">
                <Navbar />
                <div className="flex items-center justify-center mt-20">
                    <div className="text-center">
                        <div className="text-6xl mb-4">⚙️</div>
                        <p className="text-xl font-medium text-gray-600">
                            Analyzing your resume...
                        </p>
                        <p className="text-gray-400 mt-2">
                            This may take a few seconds
                        </p>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-gray-100">
                <Navbar />
                <div className="max-w-2xl mx-auto mt-10 p-6">
                    <div className="bg-red-100 text-red-600 p-4 rounded">
                        {error}
                    </div>
                </div>
            </div>
        );
    }

    // Chart data
    const scoreData = [{ name: 'ATS Score', value: analysis?.atsScore }];

    const skillsData = [
        { name: 'Found', value: analysis?.extractedSkills?.length || 0 },
        { name: 'Missing', value: analysis?.missingSkills?.length || 0 }
    ];

    const skillsBarData = analysis?.extractedSkills?.map(skill => ({
        skill: skill,
        present: 1
    })) || [];

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-6xl mx-auto mt-8 p-6 space-y-6">

                {/* Header */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h2 className="text-2xl font-bold text-gray-800">
                        Resume Analysis Results
                    </h2>
                    <p className="text-gray-500 mt-1">
                        {analysis?.fileName}
                    </p>
                </div>

                {/* ATS Score + Skills Pie */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

                    {/* ATS Score Radial Chart */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <h3 className="text-lg font-semibold mb-4">
                            ATS Score
                        </h3>
                        <div className="flex items-center justify-center">
                            <div className="relative">
                                <ResponsiveContainer width={200} height={200}>
                                    <RadialBarChart
                                        innerRadius="60%"
                                        outerRadius="100%"
                                        data={scoreData}
                                        startAngle={90}
                                        endAngle={-270}>
                                        <RadialBar
                                            dataKey="value"
                                            fill={
                                                analysis?.atsScore >= 70
                                                    ? '#22c55e'
                                                    : analysis?.atsScore >= 40
                                                    ? '#f59e0b'
                                                    : '#ef4444'
                                            }
                                        />
                                    </RadialBarChart>
                                </ResponsiveContainer>
                                <div className="absolute inset-0 flex items-center justify-center">
                                    <span className="text-4xl font-bold">
                                        {analysis?.atsScore}
                                    </span>
                                </div>
                            </div>
                        </div>
                        <p className="text-center mt-2 text-gray-500">
                            out of 100
                        </p>
                    </div>

                    {/* Skills Pie Chart */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <h3 className="text-lg font-semibold mb-4">
                            Skills Overview
                        </h3>
                        <ResponsiveContainer width="100%" height={200}>
                            <PieChart>
                                <Pie
                                    data={skillsData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={80}
                                    label={({name, value}) =>
                                        `${name}: ${value}`}>
                                    {skillsData.map((entry, index) => (
                                        <Cell
                                            key={index}
                                            fill={COLORS[index % COLORS.length]}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Skills Bar Chart */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h3 className="text-lg font-semibold mb-4">
                        Extracted Skills
                    </h3>
                    {skillsBarData.length > 0 ? (
                        <ResponsiveContainer width="100%" height={200}>
                            <BarChart data={skillsBarData}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis
                                    dataKey="skill"
                                    tick={{ fontSize: 11 }}
                                    angle={-45}
                                    textAnchor="end"
                                    height={60}
                                />
                                <YAxis hide />
                                <Tooltip />
                                <Bar dataKey="present" fill="#3b82f6" />
                            </BarChart>
                        </ResponsiveContainer>
                    ) : (
                        <p className="text-gray-400">No skills extracted</p>
                    )}
                </div>

                {/* Missing Skills */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h3 className="text-lg font-semibold mb-4 text-red-600">
                        Missing Skills
                    </h3>
                    <div className="flex flex-wrap gap-2">
                        {analysis?.missingSkills?.length > 0 ? (
                            analysis.missingSkills.map((skill, index) => (
                                <span
                                    key={index}
                                    className="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm">
                                    {skill}
                                </span>
                            ))
                        ) : (
                            <p className="text-green-600">
                                No missing skills! ✅
                            </p>
                        )}
                    </div>
                </div>

                {/* Feedback */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h3 className="text-lg font-semibold mb-4">
                        ATS Feedback
                    </h3>
                    <p className="text-gray-700">{analysis?.feedback}</p>
                </div>

                {/* AI Suggestion */}
                {aiSuggestion && (
                    <div className="bg-blue-50 rounded-lg shadow p-6 border border-blue-200">
                        <h3 className="text-lg font-semibold mb-4 text-blue-700">
                            🤖 AI Suggestions (Powered by Gemini)
                        </h3>
                        <p className="text-gray-700 whitespace-pre-wrap">
                            {aiSuggestion.aiSuggestion}
                        </p>
                    </div>
                )}

                {/* Actions */}
                <div className="flex gap-4">
                    <button
                        onClick={() => navigate('/upload')}
                        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700">
                        Analyze Another Resume
                    </button>
                    <button
                        onClick={() => navigate('/dashboard')}
                        className="bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300">
                        Back to Dashboard
                    </button>
                </div>

            </div>
        </div>
    );
};

export default Results;