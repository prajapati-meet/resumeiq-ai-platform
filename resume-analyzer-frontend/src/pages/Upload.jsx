import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { resumeApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

const Upload = () => {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const { user } = useAuth();
    const navigate = useNavigate();

    const handleFileChange = (e) => {
        const selected = e.target.files[0];
        if (selected && selected.type === 'application/pdf') {
            setFile(selected);
            setError('');
        } else {
            setError('Please select a PDF file only');
        }
    };

    const handleUpload = async (e) => {
        e.preventDefault();
        if (!file) {
            setError('Please select a PDF file');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userEmail', user.email);

            const response = await resumeApi.upload(formData);
            const { resumeId } = response.data;

            // Navigate to results page with resumeId
            navigate(`/results/${resumeId}`);

        } catch (err) {
            setError(err.response?.data?.error || 'Upload failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-2xl mx-auto mt-10 p-6">
                <div className="bg-white rounded-lg shadow-md p-8">
                    <h2 className="text-2xl font-bold mb-6 text-gray-800">
                        Upload Your Resume
                    </h2>

                    {error && (
                        <div className="bg-red-100 text-red-600 p-3 rounded mb-4">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleUpload} className="space-y-6">
                        <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
                            <input
                                type="file"
                                accept=".pdf"
                                onChange={handleFileChange}
                                className="hidden"
                                id="file-upload"
                            />
                            <label
                                htmlFor="file-upload"
                                className="cursor-pointer">
                                <div className="text-gray-500">
                                    <p className="text-4xl mb-2">📄</p>
                                    <p className="text-lg font-medium">
                                        Click to select PDF
                                    </p>
                                    <p className="text-sm mt-1">
                                        Maximum file size: 10MB
                                    </p>
                                </div>
                            </label>
                            {file && (
                                <div className="mt-4 text-green-600 font-medium">
                                    ✅ {file.name} selected
                                </div>
                            )}
                        </div>

                        <button
                            type="submit"
                            disabled={loading || !file}
                            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 font-medium text-lg">
                            {loading ? 'Analyzing...' : 'Upload & Analyze'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Upload;