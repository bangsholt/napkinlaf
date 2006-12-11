#!/usr/bin/env perl -w

foreach $old (<*.old*>) {
    $new = $old;
    $new =~ s/\.old//;
    $new_html = $new;
    $new_html =~ s/\.jpg/\.html/;
    $old_html = $old;
    $old_html =~ s/\.jpg/\.html/;

    gen($new_html, $new, $old_html);
    gen($old_html, $old, $new_html);
}

sub links {
    my ($text, $html, $other, $isNew) = @_;
    my ($top, $bot);

    if (($other !~ /\.old/) == ($isNew == 0)) {
	return ($text, $text);
    } else {
	$top = qq|<a href="$other">$text</a>|;
	$bot = $top;
	$bot =~ s/">/#Bottom">/;
	return ($top, $bot);
    }
}


sub gen {
    my ($html, $img, $other) = @_;

    open(HTML, ">$html") || die("$!: $html");
    my ($newTop, $newBot) = links("Current (1.1)", $html, $other, 1);
    my ($oldTop, $oldBot) = links("Previous (1.0)", $html, $other, 0);

    print HTML <<EOF;
<html>
<head>
    <!-- Generated by makeOld.pl -->
    <title>$img</title></head>
    <link href="napkin.css" rel="stylesheet" type="text/css" title="Style">
</head>
<body>
    <table align="center">
    <tr><td align="left"><i>$newTop</i>
	<td align="center"><i><a href="index.html">Home</a></i>
        <td align="right"><i>$oldTop</i>
    <tr><td colspan="3"><img src="$img">
    <tr><td align="left"><i>$newBot</i>
	<td align="center"><i><a href="index.html">Home</a></i>
        <td align="right"><i>$oldBot</i>
    </table>
    <a name="Bottom"></a>
</body>
</html>
EOF
    close(HTML);
}
