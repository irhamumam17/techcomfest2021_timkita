<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>KARTINI - Login</title>
	<link href="css/bootstrap.min.css" rel="stylesheet">
	<link href="css/datepicker3.css" rel="stylesheet">
	<link href="css/styles.css" rel="stylesheet">
	<!--[if lt IE 9]>
	<script src="js/html5shiv.js"></script>
	<script src="js/respond.min.js"></script>
	<![endif]-->
	<style>
		body {
			padding: 0;
		}
		.login {
			height: 100vh;
			padding: 60px;

		}
	</style>
</head>
<body>

	<div class="login">
		<div class="container"><div class="svg-wrapper">
  <svg xmlns="http://www.w3.org/2000/svg">
  </svg>
  <div class="row">
	<div class="col">
		<div class="login-panel panel panel-default">
			<div class="panel-heading">Log in</div>
			<div class="panel-body">

                @if($errors->any()) 
                    <div class="alert alert-danger"> {{$errors->first()}} </div>
                @endif

                <form role="form" action="/login" method="post">
                    @csrf
                    
					<fieldset>
						<div class="form-group">
							<input class="form-control" placeholder="E-mail" name="email" type="email" autofocus="">
						</div>
						<div class="form-group">
							<input class="form-control" placeholder="Password" name="password" type="password" value="">
						</div>
						<div class="what">
							<div class="checkbox txt-black" >
								<label>
									<input name="remember" type="checkbox" value="Remember Me">Remember Me
								</label>
							</div>
							<button type="submit" class="btn btn-primary" >Login</button></fieldset>
						</div>
				</form>
			</div>
		</div>
	</div><!-- /.col-->
</div><!-- /.row -->	
</div>
		
			
		</div>
	</div>
	

<script src="js/jquery-1.11.1.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
</body>
</html>
