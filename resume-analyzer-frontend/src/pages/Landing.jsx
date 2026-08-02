import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle2, Upload, Play, TrendingUp, FileText, CheckCircle, BarChart3 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { FadeIn } from '@/components/animations/FadeIn';
import { SlideUp } from '@/components/animations/SlideUp';
import { AnimatedProgressBar } from '@/components/animations/AnimatedProgressBar';
import { NumberTicker } from '@/components/animations/NumberTicker';

export default function Landing() {
  const navigate = useNavigate();

  return (
    <div className="relative min-h-screen overflow-hidden bg-background">
      {/* Background Gradients & Blobs */}
      <div className="absolute top-0 -left-4 w-72 h-72 bg-primary/30 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob" />
      <div className="absolute top-0 -right-4 w-72 h-72 bg-secondary/30 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob" style={{ animationDelay: '2s' }} />
      <div className="absolute -bottom-8 left-20 w-72 h-72 bg-accent/30 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob" style={{ animationDelay: '4s' }} />

      <div className="relative container mx-auto px-4 pt-32 pb-16">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          
          {/* Left Side Content */}
          <div className="space-y-8 z-10">
            <SlideUp delay={0.1}>
              <h1 className="text-5xl lg:text-7xl font-bold tracking-tight text-foreground leading-tight">
                Land More Interviews with <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-secondary">AI-Powered</span> Resume Analysis
              </h1>
            </SlideUp>

            <SlideUp delay={0.2}>
              <p className="text-xl text-muted-foreground leading-relaxed">
                Upload your resume, paste the recruiter requirements, and receive an ATS score with detailed recruiter-focused insights in seconds.
              </p>
            </SlideUp>

            <SlideUp delay={0.3}>
              <div className="flex flex-wrap gap-4 pt-4">
                <Button size="lg" className="h-14 px-8 text-lg rounded-full group transition-all duration-300 shadow-[0_0_20px_rgba(37,99,235,0.4)] hover:shadow-[0_0_30px_rgba(37,99,235,0.6)]" onClick={() => navigate('/upload')}>
                  <Upload className="mr-2 h-5 w-5 group-hover:-translate-y-1 transition-transform" /> Upload Resume
                </Button>
                <Button size="lg" variant="outline" className="h-14 px-8 text-lg rounded-full group border-primary/20 hover:bg-primary/5">
                  <Play className="mr-2 h-5 w-5 text-primary group-hover:scale-110 transition-transform" /> Try Demo
                </Button>
              </div>
            </SlideUp>

            <FadeIn delay={0.5}>
              <div className="grid grid-cols-2 gap-4 pt-8">
                {[
                  "ATS Optimized",
                  "AI Powered",
                  "Recruiter Matching",
                  "Instant Feedback"
                ].map((feature, i) => (
                  <div key={i} className="flex items-center space-x-2 text-sm font-medium text-foreground/80">
                    <CheckCircle2 className="h-5 w-5 text-success" />
                    <span>{feature}</span>
                  </div>
                ))}
              </div>
            </FadeIn>
          </div>

          {/* Right Side: Animated Dashboard Preview */}
          <FadeIn delay={0.4} className="relative z-10 lg:ml-10">
            <div className="glass rounded-2xl p-6 relative overflow-hidden shadow-2xl border border-white/20">
              
              {/* Header */}
              <div className="flex justify-between items-center mb-6">
                <div className="flex items-center space-x-3">
                  <div className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <FileText className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-foreground">Resume Analysis</h3>
                    <p className="text-xs text-muted-foreground">Software Engineer Position</p>
                  </div>
                </div>
                <div className="px-3 py-1 rounded-full bg-success/10 text-success text-xs font-semibold flex items-center">
                  <CheckCircle className="w-3 h-3 mr-1" />
                  Analyzed
                </div>
              </div>

              {/* Score Section */}
              <div className="grid grid-cols-2 gap-4 mb-6">
                <motion.div 
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.6 }}
                  className="bg-white/50 dark:bg-black/20 rounded-xl p-4 border border-border"
                >
                  <p className="text-sm text-muted-foreground mb-1">ATS Score</p>
                  <div className="flex items-baseline space-x-1">
                    <span className="text-4xl font-bold text-primary">
                      <NumberTicker value={92} delay={1} />
                    </span>
                    <span className="text-sm text-muted-foreground">/ 100</span>
                  </div>
                  <AnimatedProgressBar value={92} delay={1.5} className="mt-3" />
                </motion.div>

                <motion.div 
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.8 }}
                  className="bg-white/50 dark:bg-black/20 rounded-xl p-4 border border-border"
                >
                  <p className="text-sm text-muted-foreground mb-1">Recruiter Match</p>
                  <div className="flex items-baseline space-x-1">
                    <span className="text-4xl font-bold text-secondary">
                      <NumberTicker value={85} delay={1.2} />
                    </span>
                    <span className="text-sm text-muted-foreground">%</span>
                  </div>
                  <AnimatedProgressBar value={85} color="bg-secondary" delay={1.7} className="mt-3" />
                </motion.div>
              </div>

              {/* Chart/Bars placeholder */}
              <motion.div 
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 1 }}
                className="space-y-4"
              >
                <div className="flex items-center justify-between">
                  <h4 className="font-semibold text-sm flex items-center">
                    <BarChart3 className="w-4 h-4 mr-2 text-muted-foreground" />
                    Keyword Density
                  </h4>
                </div>
                
                {['React', 'TypeScript', 'Node.js'].map((skill, i) => (
                  <div key={skill} className="space-y-1">
                    <div className="flex justify-between text-xs">
                      <span>{skill}</span>
                      <span className="text-muted-foreground">Found</span>
                    </div>
                    <AnimatedProgressBar 
                      value={100 - (i * 15)} 
                      color="bg-accent" 
                      delay={1.5 + (i * 0.2)} 
                    />
                  </div>
                ))}
              </motion.div>

              {/* Shimmer effect moving across */}
              <div className="absolute inset-0 pointer-events-none shimmer z-20" style={{ mixBlendMode: 'overlay', opacity: 0.5 }}></div>
            </div>
          </FadeIn>

        </div>
      </div>
    </div>
  );
}
