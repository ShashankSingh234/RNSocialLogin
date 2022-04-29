import { NativeModules } from 'react-native';
const { SocialLogin } = NativeModules;
interface SocialLoginInterface {
    performGoogleLogin(name: string, location: string): void;
}
export default SocialLogin as SocialLoginInterface;