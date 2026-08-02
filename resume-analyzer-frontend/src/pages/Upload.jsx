import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDropzone } from 'react-dropzone';
import { motion, AnimatePresence } from 'framer-motion';
import { UploadCloud, File as FileIcon, X, Briefcase, FileText } from 'lucide-react';
import { resumeApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/NavBar';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { SlideUp } from '@/components/animations/SlideUp';
import { FadeIn } from '@/components/animations/FadeIn';

const SUGGESTED_POSITIONS = [
  "Java Backend Developer",
  "Software Engineer",
  "Data Analyst",
  "DevOps Engineer",
  "Frontend Developer",
  "Machine Learning Engineer",
  "Cloud Engineer"
];

const Upload = () => {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [targetPosition, setTargetPosition] = useState('');
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [jobDescription, setJobDescription] = useState('');
    const { user } = useAuth();
    const navigate = useNavigate();

    const onDrop = useCallback((acceptedFiles) => {
        const selected = acceptedFiles[0];
        if (selected) {
            setFile(selected);
            setError('');
        }
    }, []);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'application/pdf': ['.pdf'],
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx']
        },
        maxSize: 10485760, // 10MB
        multiple: false
    });

    const handleUpload = async (e) => {
        e.preventDefault();
        if (!file) {
            setError('Please select a resume file');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userEmail', user?.email || 'test@example.com'); // Fallback if no user
            // We will add these in the backend later
            formData.append('targetPosition', targetPosition);
            formData.append('jobDescription', jobDescription);

            const response = await resumeApi.upload(formData);
            const { resumeId } = response.data;

            navigate(`/results/${resumeId}`);
        } catch (err) {
            setError(err.response?.data?.error || 'Upload failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const wordCount = jobDescription.trim().split(/\s+/).filter(w => w.length > 0).length;

    return (
        <div className="min-h-screen bg-background flex flex-col relative overflow-hidden">
            {/* Background elements */}
            <div className="absolute top-20 -left-20 w-96 h-96 bg-primary/10 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob pointer-events-none" />
            <div className="absolute bottom-20 -right-20 w-96 h-96 bg-secondary/10 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob pointer-events-none" style={{ animationDelay: '2s' }} />

            <Navbar />
            
            <main className="flex-1 flex flex-col items-center justify-center p-6 relative z-10">
                <div className="w-full max-w-3xl">
                    <SlideUp>
                        <div className="text-center mb-10">
                            <h1 className="text-4xl font-bold text-foreground mb-3">Analyze Your Resume</h1>
                            <p className="text-muted-foreground text-lg">Upload your resume and the job requirements for tailored insights.</p>
                        </div>
                    </SlideUp>

                    <FadeIn delay={0.2}>
                        <form onSubmit={handleUpload} className="glass rounded-3xl p-8 space-y-8 shadow-2xl border border-white/20 dark:border-white/10 relative overflow-hidden">
                            
                            {loading && (
                                <div className="absolute inset-0 bg-background/80 backdrop-blur-sm z-50 flex flex-col items-center justify-center rounded-3xl">
                                    <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mb-4"></div>
                                    <h3 className="text-xl font-semibold animate-pulse text-foreground">Analyzing Document...</h3>
                                    <p className="text-muted-foreground mt-2">Extracting keywords, matching skills, and scoring.</p>
                                </div>
                            )}

                            {error && (
                                <div className="bg-destructive/10 border border-destructive text-destructive p-4 rounded-xl flex items-center">
                                    <X className="mr-2" /> {error}
                                </div>
                            )}

                            {/* Dropzone Area */}
                            <div 
                                {...getRootProps()} 
                                className={`relative border-2 border-dashed rounded-2xl p-10 text-center transition-all duration-300 cursor-pointer overflow-hidden group
                                    ${isDragActive ? 'border-primary bg-primary/5 scale-[1.02]' : 'border-border hover:border-primary/50 hover:bg-muted/50'}
                                    ${file ? 'border-success/50 bg-success/5' : ''}
                                `}
                            >
                                <input {...getInputProps()} />
                                
                                <AnimatePresence mode="wait">
                                    {!file ? (
                                        <motion.div 
                                            key="empty"
                                            initial={{ opacity: 0, y: 10 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            exit={{ opacity: 0, y: -10 }}
                                            className="flex flex-col items-center pointer-events-none"
                                        >
                                            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                                                <UploadCloud className="w-8 h-8 text-primary" />
                                            </div>
                                            <p className="text-lg font-medium text-foreground mb-1">
                                                {isDragActive ? "Drop file here..." : "Drag & drop your resume"}
                                            </p>
                                            <p className="text-sm text-muted-foreground">
                                                or click to browse (PDF, DOCX up to 10MB)
                                            </p>
                                        </motion.div>
                                    ) : (
                                        <motion.div 
                                            key="filled"
                                            initial={{ opacity: 0, scale: 0.9 }}
                                            animate={{ opacity: 1, scale: 1 }}
                                            className="flex flex-col items-center"
                                        >
                                            <div className="w-16 h-16 rounded-full bg-success/20 flex items-center justify-center mb-4">
                                                <FileIcon className="w-8 h-8 text-success" />
                                            </div>
                                            <p className="text-lg font-medium text-success mb-2">{file.name}</p>
                                            <Button 
                                                type="button" 
                                                variant="outline" 
                                                size="sm"
                                                onClick={(e) => { e.stopPropagation(); setFile(null); }}
                                                className="rounded-full hover:bg-destructive/10 hover:text-destructive hover:border-destructive/20"
                                            >
                                                Remove File
                                            </Button>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>

                            {/* Additional Inputs */}
                            <div className="space-y-6">
                                <div className="space-y-2 relative">
                                    <label className="text-sm font-semibold flex items-center text-foreground">
                                        <Briefcase className="w-4 h-4 mr-2 text-primary" />
                                        Position Applying For
                                    </label>
                                    <Input 
                                        type="text" 
                                        placeholder="e.g., Senior Frontend Engineer" 
                                        value={targetPosition}
                                        onChange={(e) => setTargetPosition(e.target.value)}
                                        onFocus={() => setShowSuggestions(true)}
                                        onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
                                        className="h-12 bg-white/50 dark:bg-black/20 focus-visible:ring-primary"
                                    />
                                    {showSuggestions && targetPosition.length < 3 && (
                                        <div className="absolute z-10 w-full mt-1 bg-card border border-border rounded-xl shadow-lg overflow-hidden">
                                            {SUGGESTED_POSITIONS.map(pos => (
                                                <div 
                                                    key={pos} 
                                                    className="px-4 py-2 hover:bg-muted cursor-pointer text-sm"
                                                    onClick={() => setTargetPosition(pos)}
                                                >
                                                    {pos}
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <div className="flex justify-between items-center">
                                        <label className="text-sm font-semibold flex items-center text-foreground">
                                            <FileText className="w-4 h-4 mr-2 text-secondary" />
                                            Recruiter Requirements / Job Description
                                        </label>
                                        <span className="text-xs text-muted-foreground font-medium">{wordCount} words</span>
                                    </div>
                                    <Textarea 
                                        placeholder="Paste the recruiter requirements or complete job description here..."
                                        value={jobDescription}
                                        onChange={(e) => setJobDescription(e.target.value)}
                                        className="min-h-[160px] bg-white/50 dark:bg-black/20 focus-visible:ring-secondary resize-none"
                                    />
                                </div>
                            </div>

                            <Button 
                                type="submit" 
                                size="lg" 
                                disabled={loading || !file}
                                className="w-full h-14 text-lg rounded-xl shadow-lg hover:shadow-primary/25 transition-all"
                            >
                                Analyze Resume
                            </Button>

                        </form>
                    </FadeIn>
                </div>
            </main>
        </div>
    );
};

export default Upload;