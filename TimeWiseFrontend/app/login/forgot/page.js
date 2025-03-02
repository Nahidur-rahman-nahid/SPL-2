"use client"
import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Label } from '@/components/ui/label';
import { Progress } from '@/components/ui/progress';
import { Eye, EyeOff, Lock } from 'lucide-react';

const ForgotPasswordPage = () => {
  const router = useRouter();
  
  // Form states
  const [userEmail, setUserEmail] = useState('');
  const [username, setUsername] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  
  // UI states
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [codeSent, setCodeSent] = useState(false);
  
  // Password strength validation
  const validatePassword = (password) => {
    let score = 0;
    if (!password) return { score, message: "Password is required" };
    
    // Length check
    if (password.length >= 8) score += 20;
    
    // Character variety checks
    if (/[A-Z]/.test(password)) score += 20; // Has uppercase
    if (/[a-z]/.test(password)) score += 20; // Has lowercase
    if (/[0-9]/.test(password)) score += 20; // Has number
    if (/[^A-Za-z0-9]/.test(password)) score += 20; // Has special char
    
    let message = "";
    if (score < 40) message = "Weak password";
    else if (score < 80) message = "Moderate password";
    else message = "Strong password";
    
    return { score, message };
  };
  
  const getPasswordStrengthColor = (score) => {
    if (score < 40) return "bg-red-500";
    if (score < 80) return "bg-yellow-500";
    return "bg-green-500";
  };
  
  const validateEmailForm = () => {
    if (!userEmail.trim()) return 'Email is required.';
    if (!/\S+@\S+\.\S+/.test(userEmail)) return 'Please enter a valid email address.';
    return '';
  };
  
  const validateVerificationForm = () => {
    if (!userEmail.trim()) return 'Email is required.';
    if (!username.trim()) return 'Username is required.';
    if (!verificationCode.trim()) return 'Verification code is required.';
    if (!newPassword.trim()) return 'New password is required.';
    if (validatePassword(newPassword).score < 40) return 'Please create a stronger password.';
    return '';
  };

  const handleRequestCode = async (e) => {
    e.preventDefault();
    const errorMsg = validateEmailForm();
    if (errorMsg) {
      setError(errorMsg);
      return;
    }
    
    setIsLoading(true);
    setError('');
    
    try {
      const response = await fetch(`/api/no-auth/login/forgot?userEmail=${encodeURIComponent(userEmail)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const result = await response.text();
        throw new Error(result || 'Failed to request verification code');
      }

      setSuccess('Verification code sent! Please check your email and enter the code below.');
      setCodeSent(true);
    } catch (error) {
      setError(error.message || 'An error occurred. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleResendCode = async () => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await fetch(`/api/no-auth/login/forgot?userEmail=${encodeURIComponent(userEmail)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const result = await response.text();
        throw new Error(result || 'Failed to request verification code');
      }

      setSuccess('Verification code resent! Please check your email.');
    } catch (error) {
      setError(error.message || 'An error occurred. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyAndReset = async (e) => {
    e.preventDefault();
    const errorMsg = validateVerificationForm();
    if (errorMsg) {
      setError(errorMsg);
      return;
    }
    
    setIsLoading(true);
    setError('');

    try {
      const requestBody = {
        code: verificationCode,
        userEmail: userEmail,
        userName: username,
        updatedPassword: newPassword
      };
      
      const response = await fetch('/api/no-auth/login/forgot/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const result = await response.text();
        throw new Error(result || 'Failed to verify and reset password');
      }

      setSuccess('Password reset successfully!');
      setTimeout(() => {
        router.push('/home');
      }, 2000);
    } catch (error) {
      setError(error.message || 'An error occurred. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="w-full max-w-md">
        <Card className="w-full max-w-md">
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl font-bold text-center">
              <span className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
                TimeWise <br />
              </span> 
              Account Recovery
            </CardTitle>
            <CardDescription className="text-center">
              {!codeSent ? 'Enter your email to recover your account' : 'Enter the verification code sent to your email'}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {error && (
              <Alert variant="destructive" className="mb-4">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}
            {success && (
              <Alert className="mb-4 bg-green-50 text-green-800 border-green-200">
                <AlertDescription>{success}</AlertDescription>
              </Alert>
            )}
            
            {!codeSent ? (
              <form onSubmit={handleRequestCode} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email Address</Label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="Enter your TimeWise email"
                    value={userEmail}
                    onChange={(e) => setUserEmail(e.target.value)}
                    className="w-full"
                  />
                </div>
                <Button 
                  type="submit" 
                  className="w-full"
                  disabled={isLoading}
                >
                  {isLoading ? 'Sending...' : 'Send Verification Code'}
                </Button>
              </form>
            ) : (
              <form onSubmit={handleVerifyAndReset} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email Address</Label>
                  <Input
                    id="email"
                    type="email"
                    value={userEmail}
                    onChange={(e) => setUserEmail(e.target.value)}
                    className="w-full"
                    disabled
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="username">Username</Label>
                  <Input
                    id="userame"
                    type="text"
                    placeholder="Enter your username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full"
                  />
                  <p className="text-xs text-muted-foreground">Choose from the usernames listed in the email.</p>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="verificationCode">Verification Code</Label>
                  <Input
                    id="verificationCode"
                    type="text"
                    placeholder="Enter verification code"
                    value={verificationCode}
                    onChange={(e) => setVerificationCode(e.target.value)}
                    className="w-full"
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="newPassword">New Password</Label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                    <Input
                      id="newPassword"
                      type={showPassword ? "text" : "password"}
                      placeholder="Create new password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      className="w-full pl-10 pr-10"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-3"
                    >
                      {showPassword ? (
                        <EyeOff className="h-4 w-4 text-muted-foreground" />
                      ) : (
                        <Eye className="h-4 w-4 text-muted-foreground" />
                      )}
                    </button>
                  </div>
                  <Progress
                    value={validatePassword(newPassword).score}
                    className={`h-1 ${getPasswordStrengthColor(validatePassword(newPassword).score)}`}
                  />
                  <p className="text-xs text-muted-foreground">{validatePassword(newPassword).message}</p>
                </div>
                
                <div className="flex space-x-2">
                  <Button 
                    type="button" 
                    variant="outline"
                    onClick={handleResendCode}
                    disabled={isLoading}
                    className="flex-1"
                  >
                    Resend Code
                  </Button>
                  <Button 
                    type="submit" 
                    disabled={isLoading}
                    className="flex-1"
                  >
                    {isLoading ? 'Verifying...' : 'Verify & Reset'}
                  </Button>
                </div>
              </form>
            )}
          </CardContent>
        </Card>
        
        <div className="mt-4 text-center">
          <p className="text-sm text-muted-foreground">
            Remember your password?{" "}
            <Button
              variant="link"
              className="p-0 text-sm"
              onClick={() => router.push("/login")}
            >
              Log In
            </Button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;