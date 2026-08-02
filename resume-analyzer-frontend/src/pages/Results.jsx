import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar,
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
    PieChart, Pie, Cell, LineChart, Line
} from 'recharts';
import { motion, AnimatePresence } from 'framer-motion';
import { 
    CheckCircle2, AlertTriangle, XCircle, ArrowLeft, Lightbulb, 
    Briefcase, FileText, TrendingUp, Target, Award, ShieldAlert,
    ChevronDown, Sparkles, MessageSquare
} from 'lucide-react';

import { resumeApi, aiApi } from '../services/api';
import Navbar from '../components/NavBar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { SlideUp } from '@/components/animations/SlideUp';
import { FadeIn } from '@/components/animations/FadeIn';
import { NumberTicker } from '@/components/animations/NumberTicker';
import { AnimatedProgressBar } from '@/components/animations/AnimatedProgressBar';

const COLORS = ['#2563EB', '#7C3AED', '#06B6D4', '#10B981', '#F59E0B'];

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
            let analysisData = null;
            let attempts = 0;

            while (attempts < 10) {
                try {
                    const response = await resumeApi.getAnalysis(resumeId);
                    if (response.data.status === 'COMPLETED') {
                        analysisData = response.data;
                        break;
                    }
                } catch (e) {}
                await new Promise(r => setTimeout(r, 2000));
                attempts++;
            }

            if (analysisData) {
                setAnalysis(analysisData);
                fetchAiSuggestion(resumeId);
            } else {
                setError('Analysis is taking longer than expected');
            }
        } catch (err) {
            setError('Failed to fetch results');
        } finally {
            setLoading(false);
        }
    };

    const fetchAiSuggestion = async (id) => {
        let attempts = 0;
        while (attempts < 15) {
            try {
                const response = await aiApi.getSuggestion(id);
                if (response.data && response.data.aiSuggestion) {
                    setAiSuggestion(response.data);
                    break;
                }
            } catch (e) {}
            await new Promise(r => setTimeout(r, 3000));
            attempts++;
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-background">
                <Navbar />
                <div className="flex flex-col items-center justify-center mt-32 space-y-6">
                    <div className="relative w-24 h-24">
                        <div className="absolute inset-0 border-4 border-primary/20 rounded-full"></div>
                        <div className="absolute inset-0 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
                    </div>
                    <h2 className="text-2xl font-semibold text-foreground animate-pulse">Generating Premium Insights...</h2>
                    <p className="text-muted-foreground">Scoring ATS compatibility and matching recruiter requirements.</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-background">
                <Navbar />
                <div className="max-w-md mx-auto mt-20 text-center">
                    <AlertTriangle className="w-16 h-16 text-destructive mx-auto mb-4" />
                    <h2 className="text-2xl font-bold mb-2">Analysis Failed</h2>
                    <p className="text-muted-foreground mb-6">{error}</p>
                    <Button onClick={() => navigate('/upload')} variant="outline">Try Again</Button>
                </div>
            </div>
        );
    }

    // --- MOCK DATA FALLBACKS (Until backend is fully updated) ---
    const atsScore = analysis?.atsScore || 0;
    const recruiterMatch = analysis?.recruiterMatch || Math.min(100, atsScore + 12);
    const readiness = analysis?.readiness || Math.max(0, atsScore - 5);
    const quality = analysis?.quality || Math.min(100, atsScore + 8);

    const radarData = [
        { subject: 'Skills', A: 85, fullMark: 100 },
        { subject: 'Experience', A: 90, fullMark: 100 },
        { subject: 'Education', A: 100, fullMark: 100 },
        { subject: 'Formatting', A: 75, fullMark: 100 },
        { subject: 'Keywords', A: atsScore, fullMark: 100 },
    ];

    return (
        <div className="min-h-screen bg-background pb-20">
            <Navbar />
            
            {/* Background Effects */}
            <div className="fixed top-0 left-0 w-full h-full overflow-hidden pointer-events-none -z-10">
                <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/5 rounded-full blur-[100px]" />
                <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-secondary/5 rounded-full blur-[100px]" />
            </div>

            <main className="container mx-auto px-4 mt-8 space-y-8">
                
                {/* Header */}
                <SlideUp className="flex items-center justify-between">
                    <div>
                        <Button variant="ghost" className="mb-2 -ml-4 text-muted-foreground hover:text-foreground" onClick={() => navigate('/dashboard')}>
                            <ArrowLeft className="w-4 h-4 mr-2" /> Back to Dashboard
                        </Button>
                        <h1 className="text-3xl font-bold text-foreground">Analysis Dashboard</h1>
                        <p className="text-muted-foreground flex items-center mt-1">
                            <FileText className="w-4 h-4 mr-2" /> {analysis?.fileName || 'resume.pdf'}
                        </p>
                    </div>
                    <Button onClick={() => navigate('/upload')} className="shadow-lg shadow-primary/20 hover:shadow-primary/40">
                        <Sparkles className="w-4 h-4 mr-2" /> Analyze Another
                    </Button>
                </SlideUp>

                {/* Top Summary Cards */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <SummaryCard title="ATS Score" value={atsScore} icon={Target} color="text-primary" bg="bg-primary/10" delay={0.1} />
                    <SummaryCard title="Recruiter Match" value={recruiterMatch} icon={Briefcase} color="text-secondary" bg="bg-secondary/10" delay={0.2} />
                    <SummaryCard title="Interview Readiness" value={readiness} icon={MessageSquare} color="text-accent" bg="bg-accent/10" delay={0.3} />
                    <SummaryCard title="Resume Quality" value={quality} icon={Award} color="text-success" bg="bg-success/10" delay={0.4} />
                </div>

                <Tabs defaultValue="overview" className="space-y-8">
                    <FadeIn delay={0.5}>
                        <TabsList className="bg-muted/50 p-1 w-full justify-start overflow-x-auto h-auto">
                            <TabsTrigger value="overview" className="rounded-lg px-6 py-3">Overview</TabsTrigger>
                            <TabsTrigger value="skills" className="rounded-lg px-6 py-3">Skills & Keywords</TabsTrigger>
                            <TabsTrigger value="experience" className="rounded-lg px-6 py-3">Experience</TabsTrigger>
                            <TabsTrigger value="ai-insights" className="rounded-lg px-6 py-3 flex items-center">
                                <Lightbulb className="w-4 h-4 mr-2 text-warning" /> AI Insights
                            </TabsTrigger>
                        </TabsList>
                    </FadeIn>

                    {/* OVERVIEW TAB */}
                    <TabsContent value="overview" className="space-y-6 outline-none">
                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                            
                            {/* Radar Chart */}
                            <Card className="glass lg:col-span-1">
                                <CardHeader>
                                    <CardTitle className="text-lg">ATS Breakdown</CardTitle>
                                </CardHeader>
                                <CardContent className="h-[300px] flex items-center justify-center">
                                    <ResponsiveContainer width="100%" height="100%">
                                        <RadarChart cx="50%" cy="50%" outerRadius="70%" data={radarData}>
                                            <PolarGrid stroke="hsl(var(--border))" />
                                            <PolarAngleAxis dataKey="subject" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                                            <Radar name="Score" dataKey="A" stroke="hsl(var(--primary))" fill="hsl(var(--primary))" fillOpacity={0.4} />
                                            <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--background))', borderColor: 'hsl(var(--border))' }} />
                                        </RadarChart>
                                    </ResponsiveContainer>
                                </CardContent>
                            </Card>

                            {/* ATS Issues */}
                            <Card className="glass lg:col-span-2">
                                <CardHeader>
                                    <CardTitle className="text-lg flex items-center">
                                        <ShieldAlert className="w-5 h-5 mr-2 text-destructive" /> 
                                        Critical ATS Issues
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-3">
                                        {(analysis?.missingSkills?.length > 0) ? (
                                            analysis.missingSkills.slice(0, 4).map((issue, i) => (
                                                <div key={i} className="flex items-start p-3 bg-destructive/5 border border-destructive/20 rounded-xl">
                                                    <XCircle className="w-5 h-5 text-destructive mr-3 mt-0.5 shrink-0" />
                                                    <div>
                                                        <h4 className="font-semibold text-foreground text-sm">Missing Keyword: {issue}</h4>
                                                        <p className="text-xs text-muted-foreground mt-1">This skill is heavily required by the job description but missing from your resume.</p>
                                                    </div>
                                                </div>
                                            ))
                                        ) : (
                                            <div className="flex items-center p-4 bg-success/10 text-success rounded-xl border border-success/20">
                                                <CheckCircle2 className="w-5 h-5 mr-3" /> No critical issues found!
                                            </div>
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>

                    {/* SKILLS TAB */}
                    <TabsContent value="skills" className="space-y-6 outline-none">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <Card className="glass border-success/20">
                                <CardHeader>
                                    <CardTitle className="text-success flex items-center">
                                        <CheckCircle2 className="w-5 h-5 mr-2" /> Matched Skills
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="flex flex-wrap gap-2">
                                    {analysis?.extractedSkills?.map((skill, i) => (
                                        <motion.div key={i} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: i * 0.05 }}>
                                            <Badge variant="outline" className="bg-success/10 text-success border-success/20 px-3 py-1">
                                                {skill}
                                            </Badge>
                                        </motion.div>
                                    ))}
                                    {(!analysis?.extractedSkills || analysis.extractedSkills.length === 0) && (
                                        <p className="text-muted-foreground text-sm">No specific skills extracted.</p>
                                    )}
                                </CardContent>
                            </Card>

                            <Card className="glass border-destructive/20">
                                <CardHeader>
                                    <CardTitle className="text-destructive flex items-center">
                                        <XCircle className="w-5 h-5 mr-2" /> Missing Skills
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="flex flex-wrap gap-2">
                                    {analysis?.missingSkills?.map((skill, i) => (
                                        <motion.div key={i} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: i * 0.05 }}>
                                            <Badge variant="outline" className="bg-destructive/10 text-destructive border-destructive/20 px-3 py-1">
                                                {skill}
                                            </Badge>
                                        </motion.div>
                                    ))}
                                    {(!analysis?.missingSkills || analysis.missingSkills.length === 0) && (
                                        <p className="text-success text-sm">Perfect match! No missing skills detected.</p>
                                    )}
                                </CardContent>
                            </Card>
                        </div>
                    </TabsContent>

                    {/* EXPERIENCE TAB */}
                    <TabsContent value="experience" className="outline-none">
                        <Card className="glass">
                            <CardHeader>
                                <CardTitle>Experience Analysis & Feedback</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="prose dark:prose-invert max-w-none text-muted-foreground whitespace-pre-wrap">
                                    {analysis?.feedback || "No detailed feedback available."}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    {/* AI INSIGHTS TAB */}
                    <TabsContent value="ai-insights" className="outline-none">
                        <Card className="glass overflow-hidden relative">
                            {/* Animated AI glow background */}
                            <div className="absolute -top-40 -right-40 w-80 h-80 bg-primary/20 rounded-full blur-[80px] pointer-events-none" />
                            
                            <CardHeader>
                                <CardTitle className="flex items-center text-xl">
                                    <Sparkles className="w-5 h-5 mr-2 text-warning" /> AI Assistant Insights
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                {aiSuggestion ? (
                                    <div className="bg-white/40 dark:bg-black/20 p-6 rounded-2xl border border-white/20 whitespace-pre-wrap leading-relaxed text-foreground/90">
                                        {/* Simulating typing effect for AI response */}
                                        <motion.div
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            transition={{ duration: 1 }}
                                        >
                                            {aiSuggestion.aiSuggestion}
                                        </motion.div>
                                    </div>
                                ) : (
                                    <div className="p-12 text-center flex flex-col items-center justify-center text-muted-foreground border border-dashed rounded-xl">
                                        <div className="w-8 h-8 border-4 border-warning border-t-transparent rounded-full animate-spin mb-4"></div>
                                        <p>Generating AI insights based on your target role...</p>
                                        <p className="text-sm mt-2 opacity-70">This usually takes about 10-15 seconds.</p>
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    </TabsContent>
                </Tabs>
            </main>
        </div>
    );
};

function SummaryCard({ title, value, icon: Icon, color, bg, delay }) {
    return (
        <SlideUp delay={delay}>
            <div className="glass p-6 rounded-2xl border border-white/20 shadow-sm relative overflow-hidden group hover:shadow-lg transition-all duration-300">
                <div className="flex justify-between items-start mb-4">
                    <p className="text-sm font-medium text-muted-foreground">{title}</p>
                    <div className={`p-2 rounded-lg ${bg} ${color}`}>
                        <Icon className="w-5 h-5" />
                    </div>
                </div>
                <div className="flex items-baseline space-x-1">
                    <span className={`text-4xl font-bold ${color}`}>
                        <NumberTicker value={value} delay={delay + 0.5} />
                    </span>
                    <span className="text-sm text-muted-foreground">/ 100</span>
                </div>
                <AnimatedProgressBar value={value} color={`bg-current ${color}`} delay={delay + 1} className="mt-4 opacity-50" />
            </div>
        </SlideUp>
    );
}

export default Results;