var Q={
	getQuestion:function(){
		$.getJSON( "../../question/math_basic/random", function( data ) {
			console.log(data);
		}).fail(function() {
		    console.log( "error" );
		});
	}
};
