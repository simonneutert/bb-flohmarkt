# bb-flohmarkt<!-- omit in toc -->

This is a AI/HI dream of very simple scraper for a hypothetical website.  
The code is an excercise to get better in Clojure and was built using [babashka](https://www.babashka.org).

If this code could fly, its ability would be to scrape a website, then write the data to a JSON file.

## BIG BIG Spoiler Alert 🚨<!-- omit in toc -->

This is a fantasy project! The code did never run and was developed blindfolded just for fun.  
So... I can only assume it would almost work 🤷 but it most probably won't. 

> Whatever. I learned a lot.  
>  \- Some dudes on the internet

## Then why? 🤔<!-- omit in toc -->

**YOU CAN** use this repo/code as an example of a babashka CLI tool that shells out to sth, or in this case `htmlq`. Tweak some things and you can use it to scrape your personal website - which is always the safest way to practice scraping.  
**AND/OR** have this repo up your sleeve as an example for one of many ways of hooking up tests with babashka (in this case as a `bb task`). 😮‍💨💦

## Now, some headlines of the stuff you can read next<!-- omit in toc -->

- [Requirements](#requirements)
- [Usage](#usage)
- [Run with Docker](#run-with-docker)
  - [Save the output JSON to a file](#save-the-output-json-to-a-file)
- [FAQ](#faq)
  - [I tried to run it, but it doesn't work. What's wrong?](#i-tried-to-run-it-but-it-doesnt-work-whats-wrong)
  - [Can I use this code to scrape other websites?](#can-i-use-this-code-to-scrape-other-websites)
  - [How do these hypothetical/mythical pro users work? Why is it tough to get their ID?](#how-do-these-hypotheticalmythical-pro-users-work-why-is-it-tough-to-get-their-id)
  - [If you could dream of a way to find the id/user-Id/account-id/id-id-id2d2 ... how would you do it?](#if-you-could-dream-of-a-way-to-find-the-iduser-idaccount-idid-id-id2d2--how-would-you-do-it)
    - [The easy way](#the-easy-way)
    - [The docker way](#the-docker-way)
    - [The hard way](#the-hard-way)
  - [It doesn't work! What can I do?](#it-doesnt-work-what-can-i-do)
- [batteries included / babashka tasks](#batteries-included--babashka-tasks)
  - [Run the (few) tests](#run-the-few-tests)
  - [Run the formatter](#run-the-formatter)

## Requirements

- [Babashka](https://babashka.org)
- optional: JAVA ☕️ and asdf-vm

## Usage

`$ bb scrape --help`

```bash
  alias option      ref     default           description
  -h,   --help      help    false             Prints this help message.
  -d,   --verbose   verbose false             Prints debug messages.
  -u,   --user-id           <none>            User ID of the pro account.
  -s,   --max-sleep         15                Maximum sleep time in seconds.
  -p,   --pages             Integer.MAX_VALUE Maximum number of pages to crawl.
  -t,   --to-json           false             Instead of clojure data, output JSON.
```

## Run with Docker

- clone the project
- `$ docker buildx build . -t bbflohmarkt`
- `$ docker run --rm bbflohmarkt bb --prn scrape --user-id <user-id>`

### Save the output JSON to a file

`$(echo docker run --rm ek bb --prn scrape --user-id 59191051 --to-json true) > result.json`

## FAQ

You might have some questions or dreams you need to clarify.

### I tried to run it, but it doesn't work. What's wrong?

This project was never meant to be run. It was developed blindfolded at night, no lights, just for fun. I told you that already.

### Can I use this code to scrape other websites?

I am a Nerd, not a lawyer.

So... I guess ... you should check the Terms of Service of the website you want to scrape before doing so.  
But I wouldn't be too optimistic about companies being very happy to hand out their data without showing you ads or the visitor being a soulless bot.

### How do these hypothetical/mythical pro users work? Why is it tough to get their ID?

In a dream I had, pro user default to be rendered as a JS/AJAX based page.  
This makes it mandatory to have or better be a browser with JS enabled to load and render the content.

You must know, neither Babashka nor htmlq can render JavaScript.

### If you could dream of a way to find the id/user-Id/account-id/id-id-id2d2 ... how would you do it?

Uff, tough question. Well I can only make some stuff up for you... 🤔

I couldn't so I asked ChatGPT to come up with three potential ways, here what it replied:

#### The easy way

- copy a Pro Account's url to one of the offered articles
- `$ bb --prn find-pro-accounts-user-id --url "<url>"`

#### The docker way

- copy a Pro Account's url to one of the offered articles
- `$ docker buildx build . -t bbflohmarkt`
- `$ docker run --rm bbflohmarkt bb --prn find-pro-accounts-user-id --url "<url>" -v true`

#### The hard way

- use your browser' developer tools (it should be FireFox and you know why!)
- visit a pro account's dealer page for that dream of a website you keep having
- open the developer tools and search for the value a data elem called `posterid` or `poposterid` or `pugsnoutid` has assigned to it

### It doesn't work! What can I do?

Browse the web for some cat content?! 😉😏

## batteries included / babashka tasks

### Run the (few) tests

```bash
$ bb test
```

### Run the formatter

```bash
$ bb format-zprint -lsfw **/*.clj
```

Formats using zprint, check out [zprint's babashka docs](https://github.com/kkinnear/zprint/blob/main/doc/getting/babashka.md)
