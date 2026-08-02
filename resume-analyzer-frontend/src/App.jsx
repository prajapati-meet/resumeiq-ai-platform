import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Upload from './pages/Upload';
import Results from './pages/Results';
import Landing from './pages/Landing';

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    {/* Public routes */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Protected routes — require JWT token */}
                    <Route path="/dashboard" element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    } />
                    <Route path="/upload" element={
                        <ProtectedRoute>
                            <Upload />
                        </ProtectedRoute>
                    } />
                    <Route path="/results/:resumeId" element={
                        <ProtectedRoute>
                            <Results />
                        </ProtectedRoute>
                    } />

                    {/* Public Landing */}
                    <Route path="/" element={<Landing />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;