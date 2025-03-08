"use client"

import React, { useState, useEffect } from 'react';
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Search, UserCircle2, Mail, Star } from 'lucide-react';
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { useRouter } from 'next/navigation';

export default function SearchPage() {
    
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);
    const [error, setError] = useState(null);
    const router = useRouter();
    
    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            if (searchTerm.trim().length > 0) { 
                performSearch();
            } else {
                setSearchResults([]);
            }
        }, 500);
    
        return () => clearTimeout(delayDebounceFn);
    }, [searchTerm]);

    // Search function
    const performSearch = async () => {
        setIsSearching(true);
        setError(null);

        try {
            const response = await fetch(`/api/search?keyWord=${encodeURIComponent(searchTerm)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('Search failed');
            }

            const data = await response.json();
            setSearchResults(data);
        } catch (err) {
            setError('Failed to perform search. Please try again.');
            console.error(err);
        } finally {
            setIsSearching(false);
        }
    };

    // Follow/Unfollow handler (placeholder)
    const handleFollowToggle = async (userName) => {
        try {
            const response = await fetch(`/api/users/follow?userName=${encodeURIComponent(userName)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                
            });

            if (response.ok) {
                // Update local state to reflect follow status
                setSearchResults(prev => 
                    prev.map(result => 
                        result.userName === userName 
                        ? { ...result, isFollowing: !result.isFollowing } 
                        : result
                    )
                );
            }
        } catch (error) {
            console.error('Follow/Unfollow failed', error);
        }
    };
    const handleCardClick = (userName) => { 
        
        localStorage.setItem("UserAccountName", userName);
        router.push("/home/account");
    };

    return (
        <div className="container mx-auto p-6 max-w-4xl">
            <Card className="w-full">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                        <Search className="h-6 w-6" />
                        <span>Advanced User Search</span>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    {/* Search Input */}
                    <div className="relative mb-4">
                        <Input 
                            type="text" 
                            placeholder="Search users by name, email, or bio..." 
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="pr-10"
                        />
                        {isSearching && (
                            <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500">
                                <span>Searching...</span>
                            </div>
                        )}
                    </div>

                    {/* Search Results */}
                    {error && (
                        <div className="text-red-500 mb-4">
                            {error}
                        </div>
                    )}

                    <ScrollArea className="h-[500px] w-full rounded-md border p-4">
                        {searchResults.length === 0 && !isSearching ? (
                            <div className="text-center text-gray-500">
                                No results found. Try a different search term.
                            </div>
                        ) : (
                            <div className="space-y-4">
            {searchResults.map((result, index) => (
                <Card 
                    key={index} 
                    className="hover:bg-black transition-colors cursor-pointer" // Add cursor-pointer for visual feedback
                    onClick={() => handleCardClick(result.userName)} // Add onClick handler
                >
                    <CardContent className="flex items-center justify-between p-4">
                        <div className="flex items-center space-x-4">
                            <UserCircle2 className="h-10 w-10 text-gray-400" />
                            <div>
                                <div className="flex items-center space-x-2">
                                    <h3 className="font-semibold">{result.userName}</h3>
                                    <Badge variant={result.isFollowing ? "secondary" : "outline"}>
                                        {result.isFollowing ? 'Following' : 'Not Following'}
                                    </Badge>
                                </div>
                                <div className="flex items-center space-x-2 text-sm text-gray-500">
                                    <Mail className="h-4 w-4" />
                                    <span>{result.userEmail}</span>
                                </div>
                                <p className="text-sm text-gray-600 mt-1">{result.shortBioData}</p>
                            </div>
                        </div>
                        <div className="flex items-center space-x-2">
                        <TooltipProvider>
    <Tooltip>
        <TooltipTrigger asChild>
            <Button
                variant={result.isFollowing ? "outline" : "default"}
                onClick={(e) => {
                    e.stopPropagation(); // Prevent click event propagation
                    handleFollowToggle(result.userName);
                }}
            >
                {result.isFollowing ? 'Unfollow' : 'Follow'}
            </Button>
        </TooltipTrigger>
        <TooltipContent>
            <p>{result.isFollowing ? 'Unfollow this user' : 'Follow this user'}</p>
        </TooltipContent>
    </Tooltip>
</TooltipProvider>

                            <div className="flex items-center text-yellow-500">
                                <Star className="h-5 w-5 mr-1" />
                                <span className="text-sm">{result.matchScore.toFixed(2)}</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            ))}
        </div>
                        )}
                    </ScrollArea>
                </CardContent>
            </Card>
        </div>
    );
}