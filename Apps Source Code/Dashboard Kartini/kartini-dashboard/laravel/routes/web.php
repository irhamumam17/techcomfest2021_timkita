<?php
use App\Http\Controllers\FirebaseController;
use App\Http\Controllers\AuthController;
use App\Http\Middleware\LoggedIn;
/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/
Route::get('/', function () {
    return view('welcome');
});
Route::get('/login', [AuthController::class, 'login']);
Route::post('/login', [AuthController::class, 'postLogin'])->name('login');
Route::middleware([LoggedIn::class])->group(function () {
    Route::get('/firebase', [FirebaseController::class, 'index'])->name('admin');
    Route::get('/firebase/filter', [FirebaseController::class, 'filter']);
    // yang {id} itu nanti masuk ke parameter di function controller
    Route::post('/firebase/filter/{id}', [FirebaseController::class, 'postFilter']);
    Route::get('/firebase/ajax/{id}', [FirebaseController::class, 'ajaxData']);
    Route::post('/firebase/{id}', [FirebaseController::class, 'post']);
    Route::get('/firebase/polisi', [FirebaseController::class, 'indexPolisi'])->name('polisi');
    Route::post('/firebase/proses/{id}/{uid}', [FirebaseController::class, 'postAction']);
    Route::get('/firebase/rumahsakit', [FirebaseController::class, 'indexRumahsakit'])->name('rumahsakit');


}) ;
