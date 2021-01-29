<div id="sidebar-collapse" class="col-sm-3 col-lg-2 sidebar">
    <div class="profile-sidebar">
        <div class="profile-userpic">
            <img src="http://placehold.it/50/30a5ff/fff" class="img-responsive" alt="">
        </div>
        <div class="profile-usertitle">
            <div class="profile-usertitle-name">{{request()->user['nama']}}</div>
            <div class="profile-usertitle-status"><span class="indicator label-success"></span>Online</div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="divider"></div>
    {{-- <form role="search">
        <div class="form-group">
            <input type="text" class="form-control" placeholder="Search">
        </div>
    </form> --}}
    <ul class="nav menu">
        @if (request()->user['role']== 1)
        <li class="{{$action=='index'?'active':''}}"><a href="/firebase"><em class="fa fa-bar-chart">&nbsp;</em> Verifikasi Kasus</a></li>
        <li class="{{$action=='filter'?'active':''}}"><a href="http://localhost:8000/firebase/filter">
            <em class="fa fa-navicon">&nbsp;</em> Filter Kasus 
            </a>
        </li>
        @elseif  (request()->user['role']== 2)
        <li class="{{$action=='filter'?'active':''}}"><a href="http://localhost:8000/firebase/polisi">
            <em class="fa fa-navicon">&nbsp;</em> Filter Kasus 
            </a>
        </li>
        @elseif  (request()->user['role']== 3)
        <li class="{{$action=='filter'?'active':''}}"><a href="http://localhost:8000/firebase/polisi">
            <em class="fa fa-navicon">&nbsp;</em> Filter Kasus 
            </a>
        </li>
        
        @endif


        

        <li><a href="/login"><em class="fa fa-power-off">&nbsp;</em> Logout</a></li>
    </ul>
</div>