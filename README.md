# NAPPA

Implementation of a navigation-aware technique for personalized prefetching of network requests of Android apps.
A description of the internals of the NAPPA approach is available in our [ICSE 2019 publication](http://www.ivanomalavolta.com/files/papers/ICSE_2019_NAPPA.pdf).  

## Introduction

In order to perform a Navigation-Aware prefetching, NAPPA introduces the notion of an Extended Navigation Graph.  
This graph is built by letting nodes represent activities, and all edges represent activity transitions within the application. 
The ENG plays a central role in the prefetching process by exposing prefetch enabled URLs  ( **What** ) and by keeping track of which prefetch enabled URL Candidates are most likely to be accessed in subsequent activity transitions ( **When**).

<p align="center">
	<img src="docs/img/ENG_A.png" alt="Extended Navigation Graph" width="500"/>
</p>

An URL may be composed of both static components (for example the domain name and path) and also  dynamic components (such as URL parameters).
The ENG addresses the challenge of identifying dynamic URL components by capturing [intent extras](https://developer.android.com/reference/android/content/Intent) between activity transitions.   
Whenever an HTTP Request is performed, NAPPA verifies if any of the extras captured on previous activity transitions corresponds to an URL component.  
If so, an association between an extra and its corresponding static URL components is created—this becomes a prefetch enabled URL candidate. 

## How to cite NAPPA

If NAPPA is helping your research, consider to cite is as follows, thanks!

``` 
@inproceedings{ICSE_2019_NAPPA,
	author = {Ivano Malavolta and Francesco Nocera and Patricia Lago and Marina Mongiello},
	month = {May},
	title = {{Navigation-aware and Personalized Prefetching of Network Requests in Android Apps}},
	booktitle = {Proceedings of the 41st ACM/IEEE International Conference on Software Engineering},
	year = {2019},
 	location = {Montreal, Canada},
 	numpages = {4},
 	publisher = {IEEE Press},
	url = {http://www.ivanomalavolta.com/files/papers/ICSE_2019_NAPPA.pdf}
}
```

## License

This software is licensed under the [MIT License](LICENSE).
