<!DOCTYPE html>
<html>
<head>
    
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="csrf-token" content="{{ csrf_token() }}">
	<title>Kartini - @yield('title')</title>
	<link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
	<link href="{{ asset('css/font-awesome.min.css') }}" rel="stylesheet">
	<link href="{{ asset('css/datepicker3.css') }}" rel="stylesheet">
	<link href="{{ asset('css/styles.css') }}" rel="stylesheet">
	
	<!--Custom Font-->
	<link href="https://fonts.googleapis.com/css?family=Montserrat:300,300i,400,400i,500,500i,600,600i,700,700i" rel="stylesheet">
	<!--[if lt IE 9]>
	<script src="js/html5shiv.js"></script>
	<script src="js/respond.min.js"></script>
    <![endif]-->
    <style> 
        .lds-ellipsis {
  display: inline-block;
  position: relative;
  text-align: center;
  width: 80px;
  height: 80px;
}
.lds-ellipsis div {
  position: absolute;
  top: 33px;
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #ff30ba;
  animation-timing-function: cubic-bezier(0, 1, 1, 0);
}
.lds-ellipsis div:nth-child(1) {
  left: 8px;
  animation: lds-ellipsis1 0.6s infinite;
}
.lds-ellipsis div:nth-child(2) {
  left: 8px;
  animation: lds-ellipsis2 0.6s infinite;
}
.lds-ellipsis div:nth-child(3) {
  left: 32px;
  animation: lds-ellipsis2 0.6s infinite;
}
.lds-ellipsis div:nth-child(4) {
  left: 56px;
  animation: lds-ellipsis3 0.6s infinite;
}
@keyframes lds-ellipsis1 {
  0% {
    transform: scale(0);
  }
  100% {
    transform: scale(1);
  }
}
@keyframes lds-ellipsis3 {
  0% {
    transform: scale(1);
  }
  100% {
    transform: scale(0);
  }
}
@keyframes lds-ellipsis2 {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(24px, 0);
  }
}
    </style>
</head>
<body>
    
    <nav class="navbar navbar-custom navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#sidebar-collapse"><span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span></button>
                <a class="navbar-brand" href="#"><span>KARTINI</span>Admin</a>
            </div>
        </div><!-- /.container-fluid -->
    </nav>
 



    <div class="col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main">
        <div class="row">
            <ol class="breadcrumb">
                <li><a href="#">
                    <em class="fa fa-home"></em>
                </a></li>
                <li class="active">Kasus</li>
            </ol>
        </div><!--/.row-->
        @yield('content')
    </div>    

</body>

<script src="{{ asset('js/jquery-1.11.1.min.js')}}"></script>
<script src="{{ asset('js/bootstrap.min.js')}}"></script>
<script src="{{ asset('js/chart.min.js')}}"></script>
<script src="{{ asset('js/chart-data.js')}}"></script>
<script src="{{ asset('js/easypiechart.js')}}"></script>
<script src="{{ asset('js/easypiechart-data.js')}}"></script>
<script src="{{ asset('js/bootstrap-datepicker.js')}}"></script>
<script src="{{ asset('js/custom.js')}}"></script>

<script> $(document).ready(function(){
    $(".report-data").click(function(x){
        let id = $(this).attr('id');
        $.ajaxSetup({
    headers: {
        'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
    }
});
        $.ajax({
            url: "http://localhost:8000/firebase/ajax/"+id,
            method: "get",
            // data: "_token="+"{{csrf_token()}}",
            success: function(data){
                $('.lds-ellipsis').hide();
                $('.table-report, .table-user').show();
                console.log(data);
                $('.report-nama').html(data.user.fullName);
                $('.report-email').html(data.user.email);
                $('.report-nohp').html(data.user.phoneNumber);
                $('.report-isi').html(data.text);
                $('.report-title').html(data.title);
                $('.report-file img').attr('src',data.uri);
            }
        });
    });
    $('.close-modal').click(function(){
        $('.lds-ellipsis').show();
        $('.table-report, .table-user').hide();
    });

    $('#filter-tabel').on('change', function() {
        if(this.value == 'status-semua')
        {
            $('#tabel-data tbody tr').show();

        }
        else  {
            $('#tabel-data tbody tr').hide();
            $('.'+this.value).show();
        }
    })

    $('#filter-index').on('change', function() {
        if(this.value == 'verify-semua')
        {
            $('#tabel-data tbody tr').show();

        }
        else  {
            $('#tabel-data tbody tr').hide();
            $('.'+this.value).show();
        }
    })

    $('#filter-proses').on('change', function() {
        if(this.value == 'proses-semua')
        {
            $('#tabel-data tbody tr').show();

        }
        else  {
            $('#tabel-data tbody tr').hide();
            $('.'+this.value).show();
        }
    })


}); </script>


</html>
