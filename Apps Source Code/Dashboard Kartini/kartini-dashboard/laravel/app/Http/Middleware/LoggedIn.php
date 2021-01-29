<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

class LoggedIn
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle(Request $request, Closure $next)
    {
        if ($request->session()->has('user')) {
            $user = $request->session()->get('user');
            $request->user=$user;
            return $next($request);
        }
        else {
            return redirect('login');
        }
        
    }
}
