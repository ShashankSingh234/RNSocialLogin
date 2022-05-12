import { NativeModules, Platform } from 'react-native';
import { AddScopesParams, ConfigureParams, googleSignInConfig, HasPlayServicesParams, User } from './GoogleSignInInterfaces';

const { GoogleLoginModule } = NativeModules;

interface GoogleLoginInterface {
    
    performGoogleLogin() : Promise<User>;
    hasPlayServices(options: HasPlayServicesParams) : Promise<boolean>;
    configure(options: ConfigureParams): void;
    addScopes(options: AddScopesParams): Promise<User | null>
    signInSilently(): Promise<User>;
    signOut(): Promise<null>;
    revokeAccess(): Promise<null>;
    isSignedIn(): Promise<boolean>;
    getCurrentUser(): Promise<User | null>;
    clearCachedAccessToken(tokenString: string): Promise<null>
    getToken(): Promise<{ idToken: string; accessToken: string }>;

}

class GoogleLogin implements GoogleLoginInterface
{
    async performGoogleLogin(config: googleSignInConfig = {}): Promise<User> {
        console.log("OOOOO");
        var t = await GoogleLoginModule.performGoogleLogin(config);
        console.log("******");
        console.log(t);
        return t;
    }

    hasPlayServices(options: HasPlayServicesParams): Promise<boolean> {
        throw new Error('Method not implemented.');
    }

    configure(options: ConfigureParams): void {
        if (options.offlineAccess && !options.webClientId) {
            throw new Error('RNGoogleSignin: offline use requires server web ClientID');
        }

        GoogleLoginModule.configure(options);
    }

    addScopes(options: AddScopesParams): Promise<User | null> {
        throw new Error('Method not implemented.');
    }
    signInSilently(): Promise<User> {
        throw new Error('Method not implemented.');
    }
    signOut(): Promise<null> {
        throw new Error('Method not implemented.');
    }
    revokeAccess(): Promise<null> {
        throw new Error('Method not implemented.');
    }
    isSignedIn(): Promise<boolean> {
        throw new Error('Method not implemented.');
    }
    getCurrentUser(): Promise<User | null> {
        throw new Error('Method not implemented.');
    }
    clearCachedAccessToken(tokenString: string): Promise<null> {
        throw new Error('Method not implemented.');
    }
    getToken(): Promise<{ idToken: string; accessToken: string; }> {
        throw new Error('Method not implemented.');
    }

}

export const GoogleLoginSingleton = new GoogleLogin();