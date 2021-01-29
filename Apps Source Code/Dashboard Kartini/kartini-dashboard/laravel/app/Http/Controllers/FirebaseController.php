<?php
namespace App\Http\Controllers;
use Illuminate\Http\Request;
use Kreait\Firebase;
use Kreait\Firebase\Factory;
use Kreait\Firebase\Database;
use Kreait\Firebase\ServiceAccount;
use Kreait\Firebase\Exception\Auth\EmailExists as FirebaseEmailExists;

class FirebaseController extends Controller
{
    public function index(){
        $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
        $database = $factory->createDatabase();
        // $serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/firebase.json');
        // $firebase = (new Factory)
        //     ->withServiceAccount($serviceAccount)
        //     ->create();
        // $database = $firebase->getDatabase();
        $ref = $database->getReference('reports/');
        // $refU = $database->getReference('reports-user/');
        $key = $ref->getSnapshot()->getValue();
        // $key = $refU->getSnapshot()->getValue();
        $key_active = [];
        foreach ($key as $k) {
            # code.
            array_push($key_active, $k);
        }
        // dd($key_active);
        return view('admin.index', ['key_active' => $key_active, 'action' => 'index']);
    }


    public function filter(){
        $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
        $database = $factory->createDatabase();
        // $serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/firebase.json');
        // $firebase = (new Factory)
        //     ->withServiceAccount($serviceAccount)
        //     ->create();
        // $database = $firebase->getDatabase();
        $ref = $database->getReference('reports/')->orderByChild('verify')->equalTo(2);
        $key = $ref->getSnapshot()->getValue();
        $key_active = [];
        foreach ($key as $k) {
            # code.
            array_push($key_active, $k);
        }
        // dd($key_active);
        return view('admin.filter', ['key_active' => $key_active, 'action' => 'filter']);
    }

    // yang $id? yup
    // itu nanti nyambung ke route di url
    public function post(Request $request, $id) // kenapa pake parameter
    {
        $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
        $database = $factory->createDatabase();
        // $db = $database->getReference('reports/');
        $ref = $database->getReference('reports/'.$id);
        $value = $ref->getSnapshot()->getValue();
        // $value = $refU->getSnapshot()->getValue();
        $value['verify'] = (int)$request->status;
        $uid = $value["uid"];

        $updates = [
            'reports/'.$id => $value,
            'reports-user/'.$uid.'/'.$id => $value
        ];

        $database->getReference()->update($updates);

        return redirect()->back()->with('alert');
    }




    public function postFilter (Request $request, $id)
    {
        $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
        $database = $factory->createDatabase();
        // $db = $database->getReference('reports/');
        $ref = $database->getReference('reports/'.$id);
        $value = $ref->getSnapshot()->getValue();
        $value['status'] = (int)$request->status;
        $uid = $value["uid"];

        $updates = [
            'reports/'.$id => $value,
            'reports-user/'.$uid.'/'.$id => $value
        ];

        $database->getReference()->update($updates);

        return redirect()->back()->with('alert');
    }

    public function ajaxData (Request $request, $id)
    {
        $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
        $database = $factory->createDatabase();
        // $db = $database->getReference('reports/');
        $ref = $database->getReference('reports/'.$id);
        $value = $ref->getSnapshot()->getValue();
        $refUser = $database->getReference('users/'.$value['uid']);
        $valueUser = $refUser->getSnapshot()->getValue();
        $value['user'] = $valueUser;
        return response()->json($value);
    }

   public function indexPolisi() {
    $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
    $database = $factory->createDatabase();
    // $serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/firebase.json');
    // $firebase = (new Factory)
    //     ->withServiceAccount($serviceAccount)
    //     ->create();
    // $database = $firebase->getDatabase();
    // $ref = $database->getReference('rexorts-user/uid')->orderByChild('status')->equalTo(2);
    $ref = $database->getReference('reports/')->orderByChild('status')->equalTo(2);
    $key = $ref->getSnapshot()->getValue();
    $key_active = [];
    foreach ($key as $k) {
        # code.
        array_push($key_active, $k);
    }
    // dd($key_active);
    return view('admin.polisi', ['key_active' => $key_active, 'action' => 'filter']);
   }

   public function postAction(Request $request, $id, $uid)
   {
       $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
       $database = $factory->createDatabase();
       // $db = $database->getReference('reports/');
       $ref = $database->getReference('reports/'.$id);
       $ref = $database->getReference('reports-user/'.$uid.'/'.$id);//
       $value = $ref->getSnapshot()->getValue();
       $value['proses'] = (int)$request->proses;
       //
       $value['statusTimestamp'] = (int) microtime(true) * 1000;
       $uid = $value["uid"];

       $updates = [
           'reports/'.$id => $value,
           'reports-user/'.$uid.'/'.$id => $value,
       ];

       $database->getReference()->update($updates);
       // $database->getReference()->update($updates[1]);

       return redirect()->back()->with('alert');
   }


   public function indexRumahsakit() {
    $factory = (new Factory)->withServiceAccount(__DIR__.'/firebase.json');
    $database = $factory->createDatabase();
    // $serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/firebase.json');
    // $firebase = (new Factory)
    //     ->withServiceAccount($serviceAccount)
    //     ->create();
    // $database = $firebase->getDatabase();
    $ref = $database->getReference('reports/')->orderByChild('status')->equalTo(3);
    $key = $ref->getSnapshot()->getValue();
    $key_active = [];
    foreach ($key as $k) {
        # code.
        array_push($key_active, $k);
    }
    // dd($key_active);
    return view('admin.rumahsakit', ['key_active' => $key_active, 'action' => 'filter']);
   }

}
