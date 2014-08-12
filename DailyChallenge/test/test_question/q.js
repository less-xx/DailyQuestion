var Q={
	getQuestion:function(){
		$.getJSON( "../question/random", function( data ) {
			console.log(data);
		}).fail(function() {
		    console.log( "error" );
		});
	}
};
