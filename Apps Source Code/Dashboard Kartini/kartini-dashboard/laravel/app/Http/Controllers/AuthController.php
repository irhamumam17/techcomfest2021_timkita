<?php
namespace App\Http\Controllers;
use Illuminate\Http\Request;
use Kreait\Firebase;
use Kreait\Firebase\Factory;
use Kreait\Firebase\Database;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Exception\Auth\EmailExists as FirebaseEmailExists;

class AuthController extends Controller
{
    public function login() {
        return view('login');

    }

    public function postLogin(Request $request) {
        $email = $request->email;
        $password = $request->password;
        try {
            $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json'); // ini juga, itu yang disamping diganti semua :)
            //tapi bisa direplace
            $auth = $factory->createAuth();
            $signInResult = $auth->signInWithEmailAndPassword($email, $password);
            // $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
            $database = $factory->createDatabase();
            // $db = $database->getReference('reports/');
            $ref = $database->getReference('user-web/'.$signInResult->data()['localId']);
            $value = $ref->getSnapshot()->getValue();
            $request->session()->put('user', $value);
            switch($value['role']) {
                case 1:
                    return redirect('/firebase');
                break;
                case 2:
                    return redirect('/firebase/polisi');
                break;
                case 3:
                    return redirect('/firebase/rumahsakit');
                break;

                
            }
        }
        catch(Exception $e) {
            return Redirect::back()->withErrors(['msg', 'Invalid Login']);
            
        }
        
    }
}