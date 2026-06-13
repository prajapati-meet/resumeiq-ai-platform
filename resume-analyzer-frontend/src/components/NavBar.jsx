import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="bg-blue-600 text-white px-6 py-4 flex justify-between items-center">
            <Link to="/dashboard" className="text-xl font-bold">
                Resume Analyzer
            </Link>
            <div className="flex gap-4 items-center">
                {user && (
                    <span className="text-sm">
                        Welcome, {user.fullName}
                    </span>
                )}
                <Link to="/upload"
                    className="bg-white text-blue-600 px-4 py-2 rounded hover:bg-blue-50">
                    Upload Resume
                </Link>
                <button
                    onClick={handleLogout}
                    className="bg-red-500 px-4 py-2 rounded hover:bg-red-600">
                    Logout
                </button>
            </div>
        </nav>
    );
};

export default Navbar;