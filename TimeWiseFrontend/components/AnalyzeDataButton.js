import React, { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { toast } from '@/components/ui/use-toast';
import {  Sparkles } from 'lucide-react';


export function AnalyzeDataButton({ data, buttonText = "Analyze Data" }) {
    const [openAnalysisDialog, setOpenAnalysisDialog] = useState(false);
    const [openResultDialog, setOpenResultDialog] = useState(false);
    const [analysisType, setAnalysisType] = useState("standard");
    const [context, setContext] = useState("");
    const [analysisResult, setAnalysisResult] = useState("");
    const [isLoading, setIsLoading] = useState(false);
const handleSummarize = async () => {
    try {
        setIsLoading(true);
      
        let endpoint = "/api/analyze";
        if (Array.isArray(data)) {
          endpoint = "/api/analyze/list";
        } 
        // else if (typeof data === 'object' && data !== null && !Array.isArray(data)) {
        //   endpoint = "/api/analyze/map";
        // }
      
        const url = new URL(endpoint, window.location.origin);
      
        if (context) {
          url.searchParams.append('context', context);
        }
        if (analysisType) {
          url.searchParams.append('analysisType', analysisType);
        }
      
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data), 
        });
      
        if (!response.ok) {
          throw new Error('Failed to analyze data');
        }
      
        const result = await response.json();
        setAnalysisResult(result);
        setOpenAnalysisDialog(false);
        setOpenResultDialog(true);
      } catch (error) {
        console.error("Error analyzing data:", error);
        setAnalysisResult("An error occurred while analyzing the data. Please try again.");
        setOpenAnalysisDialog(false);
        setOpenResultDialog(true);
        toast({
          title: "Error",
          description: "An error occurred while analyzing the data. Please try again.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
  };

  return (
    <>
      <Button
        onClick={() => setOpenAnalysisDialog(true)}
        variant="outline"
        className="flex items-center gap-2"
      >
        <Sparkles className="h-4 w-4 mr-1" />   {buttonText}
      </Button>

      <Dialog open={openAnalysisDialog} onOpenChange={setOpenAnalysisDialog}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>{buttonText}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <label htmlFor="analysisType" className="text-sm font-medium">Analysis Type</label>
              <Select value={analysisType} onValueChange={setAnalysisType}>
                <SelectTrigger>
                  <SelectValue placeholder="Select analysis type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="standard">Standard Analysis</SelectItem>
                  <SelectItem value="summary">Concise Summary</SelectItem>
                  <SelectItem value="detailed">Detailed Analysis</SelectItem>
                  <SelectItem value="recommendations">Key Recommendations</SelectItem>
                  <SelectItem value="metrics">Key Metrics</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="grid gap-2">
              <label htmlFor="context" className="text-sm font-medium">Context (Optional)</label>
              <Textarea
                id="context"
                placeholder="For more personalized analysis, provide context. E.g., 'These are sales figures for Q1, focus on growth trends.'"
                rows={3}
                value={context}
                onChange={(e) => setContext(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpenAnalysisDialog(false)}>Cancel</Button>
            <Button onClick={handleSummarize} disabled={isLoading}>
              {isLoading ? "Analyzing..." : "Analyze"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={openResultDialog} onOpenChange={setOpenResultDialog}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>Analysis Results</DialogTitle>
          </DialogHeader>
          <div className="max-h-96 overflow-y-auto p-4 border rounded-md bg-white dark:bg-gray-900">
  <div className="whitespace-pre-wrap text-gray-800 dark:text-gray-100">
    {analysisResult}
  </div>
</div>
          <DialogFooter>
            <Button onClick={() => setOpenResultDialog(false)}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
export default AnalyzeDataButton;