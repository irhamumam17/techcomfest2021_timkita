@extends('layouts.app')
@include('layouts.sidebar')

@section('content')

  {{-- <div name="alert" class="alert alert-success" role="alert">
	Berhasil Update Data 
  </div> --}}
<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default articles">
			<div class="panel-heading">
				
				Daftar Kasus Disetujui
				<span class="pull-right panel-button-tab-right"> 
					
						<Select class="form-control" id="filter-tabel"> 
							<option value="status-1"> Uncategorized </option>
							<option value="status-semua"> Tampilkan Semua </option>
							<option value="status-2"> Polisi </option>
							<option value="status-3"> Rumah Sakit </option>
						</Select>
					</span>
				
			</div>

			<table class="table table-striped" id="tabel-data">
				<thead>
					<th>No</th>
					<th>Nama</th>
					<th>Aksi</th>
				</thead>
				<tbody>
					<?php $no=1; ?>
					@foreach ($key_active as $item)
					
				<tr style="{{$item['status'] > 1? 'display:none;':''}}" class="status-{{$item['status']}}">
						<td style="padding-left:25px">
							<h5>	<?php echo $no; ?></h5>
							<?php $no++; ?>
						</td>
						<td> <h5><a class="report-data" id='{{ $item['id'] }}' href="#" data-toggle="modal" data-target="#exampleModalLong">{{ $item['title'] }}</a></h5></td>
						 {{-- <td>{{$k['text']}}</td> --}}
						 <td>
							<div class="btn-group" role="group">
								<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								  Action
								  <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
								  <li>
									  <form action="/firebase/filter/{{ $item['id'] }}" method="post">
									@csrf
									<input type="hidden" name="status" id="status" value="2">
									<button type="submit" class="btn btn-link btn-sm">Polisi</button>
									</form>
									</li>

								  <li>
									<form action="/firebase/filter/{{ $item['id'] }}" method="post">
										@csrf
										<input type="hidden" name="status" id="status" value="3">
										<button type="submit" class="btn btn-link btn-sm">Rumah Sakit</button>
										</form>
								  </li>
								</ul>
							  </div>
							</td>
					</tr>
					
					@endforeach
				</tbody>
				
			</table>

			<!-- Modal -->
<div class="modal fade" id="exampleModalLong" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
	<div class="modal-dialog" role="document">
	  <div class="modal-content">
		<div class="modal-header">
		  <h5 class="modal-title" id="exampleModalLongTitle">Detail Kasus</h5>
		  <button type="button" class="close close-modal" data-dismiss="modal" aria-label="Close">
			<span aria-hidden="true">&times;</span>
		  </button>
		</div>
		<div class="modal-body">
			<div class="lds-ellipsis" ><div></div><div></div><div></div><div></div></div>
			<table class="table table-report" style="display: none;">
				<tr>	<th class="report-title"> Judul Kasus </th> </tr>
				<tr>
					<td class="report-isi"> Isi Kasus </td>
				</tr>
				<tr>
					<td class="report-file"> <img style="width: 50%" /></td>
				</tr>
			</table>

			<table class="table table-user" style="display: none;">
				<th> Data Pelapor </th> <br>
				<tr>
					<td> Nama </td>
					<td> : </td>
					<td class="report-nama"> XXXXX </td>
				</tr>
				<tr>
					<td> Email</td>
					<td> : </td>
					<td class="report-email"> XXXXX </td>
				</tr>
				<tr>
					<td> No HP </td>
					<td> : </td>
					<td class="report-nohp"> XXXXX </td>
				</tr>
			</table>

		</div>
		<div class="modal-footer">
		  <button type="button" class="btn btn-primary close-modal" data-dismiss="modal">Close</button>
		</div>
	  </div>
	</div>
  </div>
			
<!--				<div class="panel-body articles-container">
				<div class="article border-bottom">
					<div class="col-xs-12">
						<div class="row">
							<div class="col-xs-10 col-md-10">
								<h4><a href="lapor1.html">Pelecehan seksual di halte Jalan Colombo</a></h4>
								<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer at sodales nisl. Donec malesuada orci ornare risus finibus feugiat.</p>
							</div>
							

							
						</div>
					</div>
					<div class="clear"></div>
				</div><End .article-->
				
				
				
				<div class="article">

					</div>
					<div class="clear"></div>
				</div><!--End .article-->
			</div>

		
	
	</div><!--/.col-->
			
	
</div><!--/.col-->
	
</div><!--/.row-->
@endsection
  

