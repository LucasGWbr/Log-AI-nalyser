import { useState } from 'react';
import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import { dracula } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import { Terminal, AlertTriangle, Play, Loader2, Code2, FileText } from 'lucide-react';

function App() {
    const [logInput, setLogInput] = useState('');
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleAnalyze = async () => {
        if (!logInput.trim()) return;

        setLoading(true);
        setResult(null);
        setError(null);

        try {
            const response = await fetch('http://localhost:8080/log/analyze', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ logContent: logInput })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || JSON.stringify(data) || 'Erro desconhecido ao contatar a IA.');
            }
            setResult(data);
        } catch (err) {
            console.error(err)
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen p-4 md:p-8 font-mono text-sm bg-slate-900 text-slate-200">
            <div className="max-w-4xl mx-auto space-y-6">

                <header className="flex items-center gap-3 border-b border-slate-800 pb-4">
                    <div className="p-2 bg-slate-800 rounded-md">
                        <Terminal className="w-6 h-6 text-blue-400" />
                    </div>
                    <div>
                        <h1 className="text-2xl font-bold text-white tracking-tight">
                            Log AI Analyser
                        </h1>
                        <p className="text-slate-400 text-xs mt-1">
                            Análise de erros com Llama 3 Local
                        </p>
                    </div>
                </header>

                <main className="space-y-4">
                    <div className="bg-slate-800 rounded-lg border border-slate-700 overflow-hidden">
            <textarea
                className="w-full h-64 bg-slate-800 p-4 text-slate-300 placeholder:text-slate-500 focus:outline-none focus:bg-slate-800/50 resize-none font-mono text-[13px] leading-relaxed scrollbar-thin"
                placeholder="Cole aqui a Stack Trace ou o log de erro para iniciar a análise... (O sistema só pega as primeiras 50 linhas)"
                value={logInput}
                onChange={(e) => setLogInput(e.target.value)}
                spellCheck="false"
            />
                    </div>

                    <div className="flex justify-end">
                        <button
                            onClick={handleAnalyze}
                            disabled={loading || !logInput}
                            className="flex items-center gap-2 px-6 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-700 disabled:text-slate-400 text-white rounded-md font-medium transition-colors shadow-sm"
                        >
                            {loading ? (
                                <>
                                    <Loader2 className="w-4 h-4 animate-spin" />
                                    <span>Analisando...</span>
                                </>
                            ) : (
                                <>
                                    <Play className="w-4 h-4 fill-current" />
                                    <span>Executar Análise</span>
                                </>
                            )}
                        </button>
                    </div>
                </main>

                {error && (
                    <div className="bg-red-900/20 border border-red-800 text-red-200 p-4 rounded-md flex items-start gap-3">
                        <AlertTriangle className="w-5 h-5 shrink-0 text-red-400 mt-0.5" />
                        <div>
                            <h3 className="font-bold mb-1">Falha na Análise</h3>
                            <p className="text-sm opacity-90">{error}</p>
                        </div>
                    </div>
                )}

                {result && (
                    <div className="space-y-6 pt-4">

                        <div className="bg-slate-800 border border-slate-700 rounded-lg overflow-hidden shadow-sm">
                            <div className="bg-slate-800/80 px-4 py-3 border-b border-slate-700 flex items-center gap-2">
                                <FileText className="w-5 h-5 text-green-400" />
                                <span className="font-bold text-slate-100">Diagnóstico</span>
                            </div>
                            <div className="p-5 text-slate-300 leading-7 text-[14px] whitespace-pre-wrap bg-slate-800/40">
                                {result.explanation}
                            </div>
                        </div>

                        {result.suggestion && (
                            <div className="bg-slate-800 border border-slate-700 rounded-lg overflow-hidden shadow-sm">
                                <div
                                    className="bg-slate-800/80 px-4 py-3 border-b border-slate-700 flex items-center justify-between gap-2">
                                    <div className="flex items-center gap-2">
                                        <Code2 className="w-5 h-5 text-indigo-400"/>
                                        <span className="font-bold text-slate-100">Correção Sugerida</span>
                                    </div>
                                </div>
                                <div
                                    className="p-5 text-slate-300 leading-7 text-[14px] whitespace-pre-wrap bg-slate-800/40">
                                    {result.suggestion}
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default App;